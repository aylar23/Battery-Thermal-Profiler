package com.aylar.batterythermalprofiler.core.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsStore(
    private val context: Context,
) {
    fun drainCoefficientMahPerMs(): Flow<Double> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.DRAIN_COEFFICIENT_MAH_PER_MS] ?: Defaults.DRAIN_COEFFICIENT_MAH_PER_MS
        }

    fun backgroundAlertThresholdMinutes(): Flow<Int> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.BACKGROUND_ALERT_THRESHOLD_MIN] ?: Defaults.BACKGROUND_ALERT_THRESHOLD_MIN
        }

    private object Defaults {
        // Very rough placeholder model; documented in Phase 4.
        const val DRAIN_COEFFICIENT_MAH_PER_MS: Double = 1e-6
        const val BACKGROUND_ALERT_THRESHOLD_MIN: Int = 60
    }

    private object Keys {
        val DRAIN_COEFFICIENT_MAH_PER_MS = doublePreferencesKey("drain_coefficient_mah_per_ms")
        val BACKGROUND_ALERT_THRESHOLD_MIN = intPreferencesKey("bg_alert_threshold_min")
    }
}

