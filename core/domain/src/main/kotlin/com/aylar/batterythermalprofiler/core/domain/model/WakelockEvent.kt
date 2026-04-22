package com.aylar.batterythermalprofiler.core.domain.model

data class WakelockEvent(
    val packageName: String,
    val tag: String,
    val acquiredAtMillis: Long,
    val durationMs: Long,
)

