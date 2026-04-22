package com.aylar.batterythermalprofiler.core.domain.repository

import com.aylar.batterythermalprofiler.core.domain.model.BatterySnapshot
import kotlinx.coroutines.flow.Flow

interface BatteryRepository {
    suspend fun insert(snapshot: BatterySnapshot)
    fun latest(): Flow<BatterySnapshot?>
    fun snapshotsBetween(startMillis: Long, endMillis: Long): Flow<List<BatterySnapshot>>
}

