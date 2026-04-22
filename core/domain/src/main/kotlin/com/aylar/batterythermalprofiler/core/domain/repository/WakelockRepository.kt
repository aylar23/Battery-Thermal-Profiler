package com.aylar.batterythermalprofiler.core.domain.repository

import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent
import kotlinx.coroutines.flow.Flow

interface WakelockRepository {
    suspend fun insert(event: WakelockEvent)
    fun recent(limit: Int): Flow<List<WakelockEvent>>
    fun eventsBetween(startMillis: Long, endMillis: Long): Flow<List<WakelockEvent>>
    fun eventsBetweenForPackage(
        packageName: String,
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<WakelockEvent>>
}

