package com.aylar.batterythermalprofiler.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aylar.batterythermalprofiler.core.data.db.entity.BatterySnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BatterySnapshotDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: BatterySnapshotEntity): Long

    @Query("SELECT * FROM battery_snapshots ORDER BY timestampMillis DESC LIMIT 1")
    fun latest(): Flow<BatterySnapshotEntity?>

    @Query(
        """
        SELECT * FROM battery_snapshots
        WHERE timestampMillis BETWEEN :startMillis AND :endMillis
        ORDER BY timestampMillis ASC
        """,
    )
    fun between(startMillis: Long, endMillis: Long): Flow<List<BatterySnapshotEntity>>

    @Query("DELETE FROM battery_snapshots WHERE timestampMillis < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long): Int
}

