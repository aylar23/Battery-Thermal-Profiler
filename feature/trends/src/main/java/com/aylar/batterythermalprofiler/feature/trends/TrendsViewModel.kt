package com.aylar.batterythermalprofiler.feature.trends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aylar.batterythermalprofiler.core.domain.repository.BatteryRepository
import com.aylar.batterythermalprofiler.core.domain.repository.ThermalRepository
import com.aylar.batterythermalprofiler.core.domain.thermal.ThermalSeverityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TrendsViewModel @Inject constructor(
    private val batteryRepository: BatteryRepository,
    private val thermalRepository: ThermalRepository,
) : ViewModel() {

    private val range = MutableStateFlow(TrendsRange.Last6h)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val batterySnapshots = range.flatMapLatest { r ->
        val end = System.currentTimeMillis()
        val start = end - r.durationMillis
        batteryRepository.snapshotsBetween(start, end)
    }

    private val thermalSnapshots = range.flatMapLatest { r ->
        val end = System.currentTimeMillis()
        val start = end - r.durationMillis
        thermalRepository.snapshotsBetween(start, end)
    }

    val uiState: StateFlow<TrendsUiState> =
        combine(range, errorMessage, batterySnapshots, thermalSnapshots) { r, err, battery, thermal ->
            val batteryPoints = battery.map { TrendsPoint(it.timestampMillis, it.level.toFloat()) }
            val tempPoints = battery.map { TrendsPoint(it.timestampMillis, it.temperatureDeciC / 10f) }

            val avgDrain = computeAvgDrainPerHour(batteryPoints)
            val zoneTimes = computeThermalZoneTimes(thermal)

            TrendsUiState(
                isLoading = false,
                errorMessage = err,
                range = r,
                batteryPoints = batteryPoints,
                tempPoints = tempPoints,
                avgDrainPerHour = avgDrain,
                thermalZoneTimes = zoneTimes,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrendsUiState(isLoading = true),
        )

    fun setRange(r: TrendsRange) = range.update { r }

    private fun computeAvgDrainPerHour(points: List<TrendsPoint>): Double? {
        if (points.size < 2) return null
        val first = points.first()
        val last = points.last()
        val dtHours = (last.xMillis - first.xMillis) / 3_600_000.0
        if (dtHours <= 0) return null
        val delta = first.y - last.y
        return delta / dtHours
    }

    private fun computeThermalZoneTimes(
        thermal: List<com.aylar.batterythermalprofiler.core.domain.model.ThermalSnapshot>,
    ): List<ThermalZoneTime> {
        if (thermal.size < 2) return emptyList()
        val sorted = thermal.sortedBy { it.timestampMillis }
        val acc = linkedMapOf<com.aylar.batterythermalprofiler.core.domain.thermal.ThermalSeverity, Long>()
        for (i in 0 until sorted.lastIndex) {
            val curr = sorted[i]
            val next = sorted[i + 1]
            val sev = ThermalSeverityMapper.fromThermalStatus(curr.thermalStatus)
            val dt = (next.timestampMillis - curr.timestampMillis).coerceAtLeast(0)
            acc[sev] = (acc[sev] ?: 0L) + dt
        }
        return acc.entries
            .sortedByDescending { it.value }
            .map { ThermalZoneTime(it.key, it.value) }
    }
}

