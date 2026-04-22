package com.aylar.batterythermalprofiler.core.data.usage

import android.app.usage.UsageStatsManager
import android.content.Context
import com.aylar.batterythermalprofiler.core.data.settings.SettingsStore
import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import com.aylar.batterythermalprofiler.core.domain.power.DrainEstimator
import com.aylar.batterythermalprofiler.core.domain.repository.AppPowerRepository
import com.aylar.batterythermalprofiler.core.domain.usage.UsageStatsCollector
import com.aylar.batterythermalprofiler.core.domain.usage.UsageTimeWindow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidUsageStatsCollector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPowerRepository: AppPowerRepository,
    private val settings: SettingsStore,
) : UsageStatsCollector {
    override suspend fun collectAndPersist(window: UsageTimeWindow) {
        val end = System.currentTimeMillis()
        val start = end - window.durationMillis

        val usageStatsManager = context.getSystemService(UsageStatsManager::class.java)
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            start,
            end,
        )

        val coeff = settings.drainCoefficientMahPerMs().first()
        val entries = stats.map { s ->
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

        appPowerRepository.upsertAll(entries)
    }
}

