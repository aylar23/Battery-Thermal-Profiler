package com.aylar.batterythermalprofiler.core.data.repository

import com.aylar.batterythermalprofiler.core.data.db.dao.BatterySnapshotDao
import com.aylar.batterythermalprofiler.core.data.mapper.toDomain
import com.aylar.batterythermalprofiler.core.data.mapper.toEntity
import com.aylar.batterythermalprofiler.core.domain.model.BatterySnapshot
import com.aylar.batterythermalprofiler.core.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomBatteryRepository @Inject constructor(
    private val dao: BatterySnapshotDao,
) : BatteryRepository {
    override suspend fun insert(snapshot: BatterySnapshot) {
        dao.insert(snapshot.toEntity())
    }

    override fun latest(): Flow<BatterySnapshot?> =
        dao.latest().map { it?.toDomain() }

    override fun snapshotsBetween(startMillis: Long, endMillis: Long): Flow<List<BatterySnapshot>> =
        dao.between(startMillis, endMillis).map { list -> list.map { it.toDomain() } }

    override suspend fun deleteOlderThan(cutoffMillis: Long): Int = dao.deleteOlderThan(cutoffMillis)
}

