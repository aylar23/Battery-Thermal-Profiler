package com.aylar.batterythermalprofiler.core.data.repository

import com.aylar.batterythermalprofiler.core.data.db.dao.WakelockEventDao
import com.aylar.batterythermalprofiler.core.data.mapper.toDomain
import com.aylar.batterythermalprofiler.core.data.mapper.toEntity
import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent
import com.aylar.batterythermalprofiler.core.domain.repository.WakelockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomWakelockRepository @Inject constructor(
    private val dao: WakelockEventDao,
) : WakelockRepository {
    override suspend fun insert(event: WakelockEvent) {
        dao.insert(event.toEntity())
    }

    override fun recent(limit: Int): Flow<List<WakelockEvent>> =
        dao.recent(limit).map { list -> list.map { it.toDomain() } }

    override fun eventsBetween(startMillis: Long, endMillis: Long): Flow<List<WakelockEvent>> =
        dao.between(startMillis, endMillis).map { list -> list.map { it.toDomain() } }

    override fun eventsBetweenForPackage(
        packageName: String,
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<WakelockEvent>> =
        dao.betweenForPackage(packageName, startMillis, endMillis)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun deleteOlderThan(cutoffMillis: Long): Int = dao.deleteOlderThan(cutoffMillis)
}

