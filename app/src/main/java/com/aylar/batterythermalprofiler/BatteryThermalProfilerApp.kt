package com.aylar.batterythermalprofiler

import android.app.Application
import com.aylar.batterythermalprofiler.work.WorkScheduler
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.aylar.batterythermalprofiler.core.data.settings.SettingsStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltAndroidApp
class BatteryThermalProfilerApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        WorkScheduler.schedulePeriodicAppPowerCollection(this)
        WorkScheduler.scheduleDailyPrune(this)

        val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val store = SettingsStore(this)
        store.collectionIntervalMinutes()
            .distinctUntilChanged()
            .onEach { WorkScheduler.schedulePeriodicAppPowerCollection(this) }
            .launchIn(appScope)
        store.dataRetentionDays()
            .distinctUntilChanged()
            .onEach { WorkScheduler.scheduleDailyPrune(this) }
            .launchIn(appScope)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

