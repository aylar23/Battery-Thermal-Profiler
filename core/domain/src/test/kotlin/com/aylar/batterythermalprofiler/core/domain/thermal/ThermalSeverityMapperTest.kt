package com.aylar.batterythermalprofiler.core.domain.thermal

import org.junit.Assert.assertEquals
import org.junit.Test

class ThermalSeverityMapperTest {
    @Test
    fun fromThermalStatus_mapsKnownConstants() {
        assertEquals(ThermalSeverity.None, ThermalSeverityMapper.fromThermalStatus(0))
        assertEquals(ThermalSeverity.Light, ThermalSeverityMapper.fromThermalStatus(1))
        assertEquals(ThermalSeverity.Moderate, ThermalSeverityMapper.fromThermalStatus(2))
        assertEquals(ThermalSeverity.Severe, ThermalSeverityMapper.fromThermalStatus(3))
        assertEquals(ThermalSeverity.Critical, ThermalSeverityMapper.fromThermalStatus(4))
        assertEquals(ThermalSeverity.Emergency, ThermalSeverityMapper.fromThermalStatus(5))
        assertEquals(ThermalSeverity.Shutdown, ThermalSeverityMapper.fromThermalStatus(6))
    }

    @Test
    fun fromThermalStatus_unknownForUnexpected() {
        assertEquals(ThermalSeverity.Unknown, ThermalSeverityMapper.fromThermalStatus(-1))
        assertEquals(ThermalSeverity.Unknown, ThermalSeverityMapper.fromThermalStatus(999))
    }
}

