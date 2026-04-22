package com.aylar.batterythermalprofiler.core.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "battery_snapshots",
    indices = [Index(value = ["timestampMillis"], unique = true)],
)
data class BatterySnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestampMillis: Long,
    val level: Int,
    val temperatureDeciC: Int,
    val voltageMv: Int,
    val status: Int,
    val plugged: Int,
)

