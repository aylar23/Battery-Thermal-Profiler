package com.aylar.batterythermalprofiler.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aylar.batterythermalprofiler.core.data.db.dao.AppPowerEntryDao
import com.aylar.batterythermalprofiler.core.data.db.dao.BatterySnapshotDao
import com.aylar.batterythermalprofiler.core.data.db.dao.ThermalSnapshotDao
import com.aylar.batterythermalprofiler.core.data.db.dao.WakelockEventDao
import com.aylar.batterythermalprofiler.core.data.db.entity.AppPowerEntryEntity
import com.aylar.batterythermalprofiler.core.data.db.entity.BatterySnapshotEntity
import com.aylar.batterythermalprofiler.core.data.db.entity.ThermalSnapshotEntity
import com.aylar.batterythermalprofiler.core.data.db.entity.WakelockEventEntity

@Database(
    entities = [
        BatterySnapshotEntity::class,
        AppPowerEntryEntity::class,
        ThermalSnapshotEntity::class,
        WakelockEventEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class BatteryDatabase : RoomDatabase() {
    abstract fun batterySnapshotDao(): BatterySnapshotDao
    abstract fun appPowerEntryDao(): AppPowerEntryDao
    abstract fun thermalSnapshotDao(): ThermalSnapshotDao
    abstract fun wakelockEventDao(): WakelockEventDao
}

