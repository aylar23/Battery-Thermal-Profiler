package com.aylar.batterythermalprofiler.core.data.repository

import com.aylar.batterythermalprofiler.core.data.db.dao.ThermalSnapshotDao
import com.aylar.batterythermalprofiler.core.data.mapper.toDomain
import com.aylar.batterythermalprofiler.core.data.mapper.toEntity
import com.aylar.batterythermalprofiler.core.domain.model.ThermalSnapshot
import com.aylar.batterythermalprofiler.core.domain.repository.ThermalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomThermalRepository @Inject constructor(
    private val dao: ThermalSnapshotDao,
) : ThermalRepository {
    override suspend fun insert(snapshot: ThermalSnapshot) {
        dao.insert(snapshot.toEntity())
    }

    override fun latest(): Flow<ThermalSnapshot?> =
        dao.latest().map { it?.toDomain() }

    override fun snapshotsBetween(startMillis: Long, endMillis: Long): Flow<List<ThermalSnapshot>> =
        dao.between(startMillis, endMillis).map { list -> list.map { it.toDomain() } }

    override suspend fun deleteOlderThan(cutoffMillis: Long): Int = dao.deleteOlderThan(cutoffMillis)
}

