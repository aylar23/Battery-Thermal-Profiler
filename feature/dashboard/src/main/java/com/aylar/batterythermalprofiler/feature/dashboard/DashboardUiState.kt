package com.aylar.batterythermalprofiler.feature.dashboard

import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import com.aylar.batterythermalprofiler.core.domain.model.BatterySnapshot
import com.aylar.batterythermalprofiler.core.domain.model.ThermalSnapshot
import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent
import com.aylar.batterythermalprofiler.core.domain.thermal.ThermalSeverity

data class DashboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val battery: BatterySnapshot? = null,
    val thermal: ThermalSnapshot? = null,
    val thermalSeverity: ThermalSeverity = ThermalSeverity.Unknown,
    val topApps: List<AppPowerEntry> = emptyList(),
    val recentWakelocks: List<WakelockEvent> = emptyList(),
    val lastUpdatedMillis: Long? = null,
)

