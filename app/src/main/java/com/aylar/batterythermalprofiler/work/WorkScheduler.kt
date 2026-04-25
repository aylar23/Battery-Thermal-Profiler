package com.aylar.batterythermalprofiler.work

import android.content.Context
import com.aylar.batterythermalprofiler.core.data.settings.SettingsStore
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val UNIQUE_NAME = "app_power_collection"
    private const val PRUNE_UNIQUE_NAME = "data_prune"

    fun schedulePeriodicAppPowerCollection(context: Context) {
        val minutes = runBlocking {
            SettingsStore(context.applicationContext).collectionIntervalMinutes().first()
        }.coerceAtLeast(15)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val request = PeriodicWorkRequestBuilder<AppPowerCollectorWorker>(minutes.toLong(), TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun scheduleDailyPrune(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val request = PeriodicWorkRequestBuilder<DataPruneWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PRUNE_UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }
}

