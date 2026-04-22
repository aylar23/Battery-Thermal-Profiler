package com.aylar.batterythermalprofiler.core.domain.thermal

/**
 * Maps Android's THERMAL_STATUS_* ints to a simplified severity.
 *
 * See: android.os.PowerManager.THERMAL_STATUS_*
 */
enum class ThermalSeverity {
    None,
    Light,
    Moderate,
    Severe,
    Critical,
    Emergency,
    Shutdown,
    Unknown,
}

