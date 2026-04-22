package com.aylar.batterythermalprofiler.core.domain.repository

import com.aylar.batterythermalprofiler.core.domain.model.ThermalSnapshot
import kotlinx.coroutines.flow.Flow

interface ThermalRepository {
    suspend fun insert(snapshot: ThermalSnapshot)
    fun latest(): Flow<ThermalSnapshot?>
    fun snapshotsBetween(startMillis: Long, endMillis: Long): Flow<List<ThermalSnapshot>>
}

