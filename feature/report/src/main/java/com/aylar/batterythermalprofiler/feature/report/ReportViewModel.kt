package com.aylar.batterythermalprofiler.feature.report

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aylar.batterythermalprofiler.core.domain.repository.AppPowerRepository
import com.aylar.batterythermalprofiler.core.domain.repository.BatteryRepository
import com.aylar.batterythermalprofiler.core.domain.repository.ThermalRepository
import com.aylar.batterythermalprofiler.core.domain.repository.WakelockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val batteryRepository: BatteryRepository,
    private val thermalRepository: ThermalRepository,
    private val appPowerRepository: AppPowerRepository,
    private val wakelockRepository: WakelockRepository,
) : ViewModel() {

    private val range = MutableStateFlow(ReportRange.Last24h)
    private val isGenerating = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val html = MutableStateFlow<String?>(null)
    private val plainSummary = MutableStateFlow<String?>(null)
    private val pdfUriString = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ReportUiState> =
        combine(
            combine(range, isGenerating, errorMessage) { r, gen, err -> Triple(r, gen, err) },
            combine(html, plainSummary, pdfUriString) { h, plain, uri -> Triple(h, plain, uri) },
        ) { a, b ->
            val (r, gen, err) = a
            val (h, plain, uri) = b
            ReportUiState(
                isGenerating = gen,
                errorMessage = err,
                range = r,
                html = h,
                plainSummary = plain,
                pdfUriString = uri,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReportUiState(),
        )

    fun setRange(r: ReportRange) = range.update { r }

    fun generateReport() {
        if (isGenerating.value) return
        viewModelScope.launch {
            isGenerating.value = true
            errorMessage.value = null
            pdfUriString.value = null
            try {
                val now = System.currentTimeMillis()
                val r = range.value
                val start = now - r.durationMillis
                val end = now

                val battery = batteryRepository.snapshotsBetween(start, end).first()
                val thermal = thermalRepository.snapshotsBetween(start, end).first()
                val wakelocks = wakelockRepository.eventsBetween(start, end).first()
                val appsTop = appPowerRepository.topByEstimatedMah(start, end, limit = 10).first()

                val avgDrain = computeAvgDrainPerHour(battery)
                val peakTempC = thermal.mapNotNull { it.cpuTempDeciC }.maxOrNull()?.div(10.0)
                val worstApp = appsTop.maxByOrNull { it.estimatedMah }

                val recommendations = buildRecommendations(
                    apps = appsTop,
                    wakelocks = wakelocks,
                )

                val reportHtml = ReportHtmlBuilder.buildHtml(
                    title = "Battery & Thermal Profiler report",
                    windowStartMillis = start,
                    windowEndMillis = end,
                    avgDrainPerHour = avgDrain,
                    peakTempC = peakTempC,
                    worstApp = worstApp,
                    apps = appsTop,
                    thermal = thermal,
                    wakelocks = wakelocks,
                    recommendations = recommendations,
                )
                val summary = ReportHtmlBuilder.buildPlainSummary(
                    windowStartMillis = start,
                    windowEndMillis = end,
                    avgDrainPerHour = avgDrain,
                    peakTempC = peakTempC,
                    worstApp = worstApp,
                )

                html.value = reportHtml
                plainSummary.value = summary

                val uri = PdfReportGenerator.renderHtmlToPdfInCache(
                    context = appContext,
                    html = reportHtml,
                    fileName = "battery-thermal-report-${r.label}.pdf",
                )
                pdfUriString.value = uri.toString()
            } catch (t: Throwable) {
                errorMessage.value = t.message ?: t.toString()
            } finally {
                isGenerating.value = false
            }
        }
    }

    private fun computeAvgDrainPerHour(battery: List<com.aylar.batterythermalprofiler.core.domain.model.BatterySnapshot>): Double? {
        if (battery.size < 2) return null
        val sorted = battery.sortedBy { it.timestampMillis }
        val first = sorted.first()
        val last = sorted.last()
        val dtHours = (last.timestampMillis - first.timestampMillis) / 3_600_000.0
        if (dtHours <= 0) return null
        val delta = first.level - last.level
        return delta / dtHours
    }

    private fun buildRecommendations(
        apps: List<com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry>,
        wakelocks: List<com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent>,
    ): List<String> {
        val out = mutableListOf<String>()
        val wakelockByPkg = wakelocks.groupBy { it.packageName }
            .mapValues { (_, events) -> events.sumOf { it.durationMs } }

        apps.take(5).forEach { app ->
            val wlMs = wakelockByPkg[app.packageName] ?: 0L
            if (wlMs >= 30L * 60 * 1000) {
                out += "App ${app.packageName} held wakelocks for ${formatDuration(wlMs)} in this window."
            }
            if (app.backgroundMs >= 2L * 60 * 60 * 1000) {
                out += "App ${app.packageName} ran in background for ${formatDuration(app.backgroundMs)}."
            }
        }
        return out.distinct()
    }

    private fun formatDuration(ms: Long): String {
        val totalSeconds = (ms / 1000).coerceAtLeast(0)
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }
}

