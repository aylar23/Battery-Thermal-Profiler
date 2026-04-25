package com.aylar.batterythermalprofiler.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aylar.batterythermalprofiler.core.data.settings.SettingsStore
import com.aylar.batterythermalprofiler.core.domain.repository.AppPowerRepository
import com.aylar.batterythermalprofiler.core.domain.repository.BatteryRepository
import com.aylar.batterythermalprofiler.core.domain.repository.ThermalRepository
import com.aylar.batterythermalprofiler.core.domain.repository.WakelockRepository
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DataPruneWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val settingsStore: SettingsStore,
    private val batteryRepository: BatteryRepository,
    private val thermalRepository: ThermalRepository,
    private val appPowerRepository: AppPowerRepository,
    private val wakelockRepository: WakelockRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val days = settingsStore.dataRetentionDays().first().coerceIn(7, 30)
        val cutoffMillis = System.currentTimeMillis() - days.toLong() * 24 * 60 * 60 * 1000

        batteryRepository.deleteOlderThan(cutoffMillis)
        thermalRepository.deleteOlderThan(cutoffMillis)
        appPowerRepository.deleteOlderThan(cutoffMillis)
        wakelockRepository.deleteOlderThan(cutoffMillis)

        return Result.success()
    }
}

