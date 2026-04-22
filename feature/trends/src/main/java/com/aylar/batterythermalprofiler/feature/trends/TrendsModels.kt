package com.aylar.batterythermalprofiler.feature.trends

import com.aylar.batterythermalprofiler.core.domain.thermal.ThermalSeverity

enum class TrendsRange(val label: String, val durationMillis: Long) {
    Last6h("6h", 6L * 60 * 60 * 1000),
    Last24h("24h", 24L * 60 * 60 * 1000),
    Last7d("7d", 7L * 24 * 60 * 60 * 1000),
}

data class TrendsPoint(
    val xMillis: Long,
    val y: Float,
)

data class ThermalZoneTime(
    val severity: ThermalSeverity,
    val durationMillis: Long,
)

data class TrendsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val range: TrendsRange = TrendsRange.Last6h,
    val batteryPoints: List<TrendsPoint> = emptyList(),
    val tempPoints: List<TrendsPoint> = emptyList(),
    val avgDrainPerHour: Double? = null,
    val thermalZoneTimes: List<ThermalZoneTime> = emptyList(),
)

