package com.aylar.batterythermalprofiler.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aylar.batterythermalprofiler.core.domain.repository.AppPowerRepository
import com.aylar.batterythermalprofiler.core.domain.repository.BatteryRepository
import com.aylar.batterythermalprofiler.core.domain.repository.ThermalRepository
import com.aylar.batterythermalprofiler.core.domain.repository.WakelockRepository
import com.aylar.batterythermalprofiler.core.domain.thermal.ThermalSeverityMapper
import com.aylar.batterythermalprofiler.core.domain.usage.UsageStatsCollector
import com.aylar.batterythermalprofiler.core.domain.usage.UsageTimeWindow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    batteryRepository: BatteryRepository,
    thermalRepository: ThermalRepository,
    appPowerRepository: AppPowerRepository,
    wakelockRepository: WakelockRepository,
    private val usageStatsCollector: UsageStatsCollector,
) : ViewModel() {

    private val refreshing = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val window = UsageTimeWindow.Last1h

    val uiState: StateFlow<DashboardUiState> =
        combine(
            batteryRepository.latest(),
            thermalRepository.latest(),
            appPowerRepository.topLatestWindow(limit = 3),
            wakelockRepository.recent(limit = 5),
            refreshing,
        ) { battery, thermal, topApps, wakelocks, isRefreshing ->
            Quint(
                battery,
                thermal,
                topApps,
                wakelocks,
                isRefreshing,
            )
        }
            .combine(errorMessage) { q, err ->
                val battery = q.a
                val thermal = q.b
                val topApps = q.c
                val wakelocks = q.d
                val isRefreshing = q.e
                val lastUpdated = battery?.timestampMillis ?: thermal?.timestampMillis
                DashboardUiState(
                    isLoading = false,
                    isRefreshing = isRefreshing,
                    errorMessage = err,
                    battery = battery,
                    thermal = thermal,
                    thermalSeverity = ThermalSeverityMapper.fromThermalStatus(thermal?.thermalStatus ?: -1),
                    topApps = topApps,
                    recentWakelocks = wakelocks,
                    lastUpdatedMillis = lastUpdated,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DashboardUiState(isLoading = true),
            )

    fun refresh() {
        viewModelScope.launch {
            refreshing.value = true
            errorMessage.value = null
            runCatching {
                usageStatsCollector.collectAndPersist(window)
            }.onFailure { e ->
                errorMessage.value = e.message ?: "Refresh failed"
            }
            refreshing.value = false
        }
    }
}

private data class Quint<A, B, C, D, E>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
)

