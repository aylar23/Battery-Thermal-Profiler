package com.aylar.batterythermalprofiler.core.domain.model

data class ThermalSnapshot(
    val timestampMillis: Long,
    val thermalStatus: Int,
    val cpuTempDeciC: Int?,
)

