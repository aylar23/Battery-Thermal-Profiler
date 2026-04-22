package com.aylar.batterythermalprofiler.core.domain.thermal

object ThermalSeverityMapper {
    fun fromThermalStatus(thermalStatus: Int): ThermalSeverity =
        when (thermalStatus) {
            0 -> ThermalSeverity.None
            1 -> ThermalSeverity.Light
            2 -> ThermalSeverity.Moderate
            3 -> ThermalSeverity.Severe
            4 -> ThermalSeverity.Critical
            5 -> ThermalSeverity.Emergency
            6 -> ThermalSeverity.Shutdown
            else -> ThermalSeverity.Unknown
        }
}

