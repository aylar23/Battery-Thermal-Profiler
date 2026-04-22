package com.aylar.batterythermalprofiler.core.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "thermal_snapshots",
    indices = [Index(value = ["timestampMillis"], unique = true)],
)
data class ThermalSnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestampMillis: Long,
    val thermalStatus: Int,
    val cpuTempDeciC: Int?,
)

