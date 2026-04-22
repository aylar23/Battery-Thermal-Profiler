package com.aylar.batterythermalprofiler.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aylar.batterythermalprofiler.core.data.db.entity.AppPowerEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppPowerEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<AppPowerEntryEntity>)

    @Query(
        """
        SELECT * FROM app_power_entries
        WHERE windowStartMillis = :windowStartMillis AND windowEndMillis = :windowEndMillis
        ORDER BY estimatedMah DESC
        LIMIT :limit
        """,
    )
    fun topByEstimatedMah(
        windowStartMillis: Long,
        windowEndMillis: Long,
        limit: Int,
    ): Flow<List<AppPowerEntryEntity>>

    @Query(
        """
        SELECT * FROM app_power_entries
        WHERE windowEndMillis = (SELECT MAX(windowEndMillis) FROM app_power_entries)
        ORDER BY estimatedMah DESC
        LIMIT :limit
        """,
    )
    fun topLatestWindow(limit: Int): Flow<List<AppPowerEntryEntity>>
}

