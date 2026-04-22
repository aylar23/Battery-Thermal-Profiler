package com.aylar.batterythermalprofiler.core.domain.usage

enum class UsageTimeWindow(val durationMillis: Long) {
    Last1h(60L * 60L * 1000L),
    Last6h(6L * 60L * 60L * 1000L),
    Last24h(24L * 60L * 60L * 1000L),
}

