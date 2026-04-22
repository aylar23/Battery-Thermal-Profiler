package com.aylar.batterythermalprofiler.core.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wakelock_events",
    indices = [
        Index(value = ["packageName"]),
        Index(value = ["acquiredAtMillis"]),
    ],
)
data class WakelockEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val tag: String,
    val acquiredAtMillis: Long,
    val durationMs: Long,
)

