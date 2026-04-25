package com.aylar.batterythermalprofiler.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aylar.batterythermalprofiler.core.data.db.entity.ThermalSnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThermalSnapshotDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ThermalSnapshotEntity): Long

    @Query("SELECT * FROM thermal_snapshots ORDER BY timestampMillis DESC LIMIT 1")
    fun latest(): Flow<ThermalSnapshotEntity?>

    @Query(
        """
        SELECT * FROM thermal_snapshots
        WHERE timestampMillis BETWEEN :startMillis AND :endMillis
        ORDER BY timestampMillis ASC
        """,
    )
    fun between(startMillis: Long, endMillis: Long): Flow<List<ThermalSnapshotEntity>>

    @Query("DELETE FROM thermal_snapshots WHERE timestampMillis < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long): Int
}

