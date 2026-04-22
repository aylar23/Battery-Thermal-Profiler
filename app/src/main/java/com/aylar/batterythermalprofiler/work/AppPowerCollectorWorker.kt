package com.aylar.batterythermalprofiler.work

import android.content.Context
import android.app.usage.UsageStatsManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aylar.batterythermalprofiler.core.data.settings.SettingsStore
import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import com.aylar.batterythermalprofiler.core.domain.power.DrainEstimator
import com.aylar.batterythermalprofiler.core.domain.repository.AppPowerRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class AppPowerCollectorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repo: AppPowerRepository,
    private val settings: SettingsStore,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        // Minimal Phase 4 implementation: collect foreground time via UsageStats.
        val end = System.currentTimeMillis()
        val start = end - WINDOW_MS_1H

        val usageStatsManager = applicationContext.getSystemService(UsageStatsManager::class.java)
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            start,
            end,
        )

        val coeff = settings.drainCoefficientMahPerMs().first()
        val entries = stats
            .filter { it.packageName != null }
            .map { s ->
                val fg = s.totalTimeInForeground
                val bg = 0L
                AppPowerEntry(
                    packageName = s.packageName,
                    uid = -1,
                    windowStartMillis = start,
                    windowEndMillis = end,
                    foregroundMs = fg,
                    backgroundMs = bg,
                    wakelocksHeld = 0,
                    estimatedMah = DrainEstimator.estimateMah(fg, bg, coeff),
                )
            }

        repo.upsertAll(entries)
        return Result.success()
    }

    companion object {
        private const val WINDOW_MS_1H = 60L * 60L * 1000L
    }
}

