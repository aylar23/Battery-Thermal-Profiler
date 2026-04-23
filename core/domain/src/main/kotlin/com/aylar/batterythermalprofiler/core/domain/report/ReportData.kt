package com.aylar.batterythermalprofiler.core.domain.report

import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import com.aylar.batterythermalprofiler.core.domain.model.BatterySnapshot
import com.aylar.batterythermalprofiler.core.domain.model.ThermalSnapshot
import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent

data class ReportData(
    val windowStartMillis: Long,
    val windowEndMillis: Long,
    val battery: List<BatterySnapshot>,
    val thermal: List<ThermalSnapshot>,
    val appPowerTop: List<AppPowerEntry>,
    val wakelocks: List<WakelockEvent>,
    val recommendations: List<String>,
) {
    val windowDurationMillis: Long get() = (windowEndMillis - windowStartMillis).coerceAtLeast(0)
}

