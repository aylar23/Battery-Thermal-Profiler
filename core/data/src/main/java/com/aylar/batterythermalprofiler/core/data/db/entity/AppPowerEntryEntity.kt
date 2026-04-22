package com.aylar.batterythermalprofiler.core.data.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "app_power_entries",
    primaryKeys = ["packageName", "windowStartMillis", "windowEndMillis"],
    indices = [
        Index(value = ["windowStartMillis", "windowEndMillis"]),
        Index(value = ["estimatedMah"]),
    ],
)
data class AppPowerEntryEntity(
    val packageName: String,
    val uid: Int,
    val windowStartMillis: Long,
    val windowEndMillis: Long,
    val foregroundMs: Long,
    val backgroundMs: Long,
    val wakelocksHeld: Int,
    val estimatedMah: Double,
)

