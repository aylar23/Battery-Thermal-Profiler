package com.aylar.batterythermalprofiler.core.domain.repository

import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import kotlinx.coroutines.flow.Flow

interface AppPowerRepository {
    suspend fun upsertAll(entries: List<AppPowerEntry>)
    fun topByEstimatedMah(
        windowStartMillis: Long,
        windowEndMillis: Long,
        limit: Int,
    ): Flow<List<AppPowerEntry>>

    fun topLatestWindow(limit: Int): Flow<List<AppPowerEntry>>

    fun entriesForLatestWindowOfDuration(
        durationMillis: Long,
    ): Flow<List<AppPowerEntry>>
}

