package com.aylar.batterythermalprofiler.core.domain.usage

interface UsageStatsCollector {
    suspend fun collectAndPersist(window: UsageTimeWindow)
}

