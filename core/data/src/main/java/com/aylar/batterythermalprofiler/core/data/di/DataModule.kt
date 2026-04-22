package com.aylar.batterythermalprofiler.core.data.di

import android.content.Context
import androidx.room.Room
import com.aylar.batterythermalprofiler.core.data.db.BatteryDatabase
import com.aylar.batterythermalprofiler.core.data.repository.RoomAppPowerRepository
import com.aylar.batterythermalprofiler.core.data.repository.RoomBatteryRepository
import com.aylar.batterythermalprofiler.core.data.repository.RoomThermalRepository
import com.aylar.batterythermalprofiler.core.data.repository.RoomWakelockRepository
import com.aylar.batterythermalprofiler.core.data.settings.SettingsStore
import com.aylar.batterythermalprofiler.core.data.usage.AndroidUsageStatsCollector
import com.aylar.batterythermalprofiler.core.domain.repository.AppPowerRepository
import com.aylar.batterythermalprofiler.core.domain.repository.BatteryRepository
import com.aylar.batterythermalprofiler.core.domain.repository.ThermalRepository
import com.aylar.batterythermalprofiler.core.domain.repository.WakelockRepository
import com.aylar.batterythermalprofiler.core.domain.usage.UsageStatsCollector
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideBatteryDatabase(
        @ApplicationContext context: Context,
    ): BatteryDatabase =
        Room.databaseBuilder(context, BatteryDatabase::class.java, "battery.db")
            .build()

    @Provides
    fun provideBatterySnapshotDao(db: BatteryDatabase) = db.batterySnapshotDao()

    @Provides
    fun provideAppPowerEntryDao(db: BatteryDatabase) = db.appPowerEntryDao()

    @Provides
    fun provideThermalSnapshotDao(db: BatteryDatabase) = db.thermalSnapshotDao()

    @Provides
    fun provideWakelockEventDao(db: BatteryDatabase) = db.wakelockEventDao()

    @Provides
    @Singleton
    fun provideSettingsStore(
        @ApplicationContext context: Context,
    ): SettingsStore = SettingsStore(context)

    @Provides
    @Singleton
    fun provideUsageStatsCollector(
        impl: AndroidUsageStatsCollector,
    ): UsageStatsCollector = impl
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindBatteryRepository(impl: RoomBatteryRepository): BatteryRepository
    @Binds abstract fun bindAppPowerRepository(impl: RoomAppPowerRepository): AppPowerRepository
    @Binds abstract fun bindThermalRepository(impl: RoomThermalRepository): ThermalRepository
    @Binds abstract fun bindWakelockRepository(impl: RoomWakelockRepository): WakelockRepository
}

