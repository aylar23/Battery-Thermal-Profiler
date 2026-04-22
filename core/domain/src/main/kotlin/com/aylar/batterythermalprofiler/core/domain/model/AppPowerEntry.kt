package com.aylar.batterythermalprofiler.core.domain.model

data class AppPowerEntry(
    val packageName: String,
    val uid: Int,
    val windowStartMillis: Long,
    val windowEndMillis: Long,
    val foregroundMs: Long,
    val backgroundMs: Long,
    val wakelocksHeld: Int,
    val estimatedMah: Double,
)

