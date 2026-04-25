package com.aylar.batterythermalprofiler.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aylar.batterythermalprofiler.core.data.db.entity.WakelockEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WakelockEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WakelockEventEntity): Long

    @Query("SELECT * FROM wakelock_events ORDER BY acquiredAtMillis DESC LIMIT :limit")
    fun recent(limit: Int): Flow<List<WakelockEventEntity>>

    @Query(
        """
        SELECT * FROM wakelock_events
        WHERE acquiredAtMillis BETWEEN :startMillis AND :endMillis
        ORDER BY acquiredAtMillis ASC
        """,
    )
    fun between(startMillis: Long, endMillis: Long): Flow<List<WakelockEventEntity>>

    @Query(
        """
        SELECT * FROM wakelock_events
        WHERE packageName = :packageName
          AND acquiredAtMillis BETWEEN :startMillis AND :endMillis
        ORDER BY acquiredAtMillis ASC
        """,
    )
    fun betweenForPackage(
        packageName: String,
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<WakelockEventEntity>>

    @Query("DELETE FROM wakelock_events WHERE acquiredAtMillis < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long): Int
}

