package com.aylar.batterythermalprofiler.core.domain.model

data class BatterySnapshot(
    val timestampMillis: Long,
    val level: Int,
    val temperatureDeciC: Int,
    val voltageMv: Int,
    val status: Int,
    val plugged: Int,
)

