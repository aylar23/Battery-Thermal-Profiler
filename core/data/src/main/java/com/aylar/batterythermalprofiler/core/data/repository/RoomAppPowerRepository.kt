package com.aylar.batterythermalprofiler.core.data.repository

import com.aylar.batterythermalprofiler.core.data.db.dao.AppPowerEntryDao
import com.aylar.batterythermalprofiler.core.data.mapper.toDomain
import com.aylar.batterythermalprofiler.core.data.mapper.toEntity
import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import com.aylar.batterythermalprofiler.core.domain.repository.AppPowerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomAppPowerRepository @Inject constructor(
    private val dao: AppPowerEntryDao,
) : AppPowerRepository {
    override suspend fun upsertAll(entries: List<AppPowerEntry>) {
        dao.upsertAll(entries.map { it.toEntity() })
    }

    override fun topByEstimatedMah(
        windowStartMillis: Long,
        windowEndMillis: Long,
        limit: Int,
    ): Flow<List<AppPowerEntry>> =
        dao.topByEstimatedMah(windowStartMillis, windowEndMillis, limit)
            .map { list -> list.map { it.toDomain() } }

    override fun topLatestWindow(limit: Int): Flow<List<AppPowerEntry>> =
        dao.topLatestWindow(limit).map { list -> list.map { it.toDomain() } }

    override fun entriesForLatestWindowOfDuration(durationMillis: Long): Flow<List<AppPowerEntry>> =
        dao.entriesForLatestWindowOfDuration(durationMillis).map { list -> list.map { it.toDomain() } }
}

