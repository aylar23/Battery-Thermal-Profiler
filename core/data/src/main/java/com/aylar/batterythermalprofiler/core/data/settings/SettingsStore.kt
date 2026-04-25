package com.aylar.batterythermalprofiler.core.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
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

    fun collectionIntervalMinutes(): Flow<Int> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.COLLECTION_INTERVAL_MIN] ?: Defaults.COLLECTION_INTERVAL_MIN
        }

    fun temperatureWarningThresholdC(): Flow<Int> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.TEMP_WARNING_THRESHOLD_C] ?: Defaults.TEMP_WARNING_THRESHOLD_C
        }

    fun dataRetentionDays(): Flow<Int> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.DATA_RETENTION_DAYS] ?: Defaults.DATA_RETENTION_DAYS
        }

    fun startOnBootEnabled(): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.START_ON_BOOT] ?: Defaults.START_ON_BOOT
        }

    suspend fun setCollectionIntervalMinutes(minutes: Int) {
        context.dataStore.edit { it[Keys.COLLECTION_INTERVAL_MIN] = minutes }
    }

    suspend fun setBackgroundAlertThresholdMinutes(minutes: Int) {
        context.dataStore.edit { it[Keys.BACKGROUND_ALERT_THRESHOLD_MIN] = minutes }
    }

    suspend fun setTemperatureWarningThresholdC(tempC: Int) {
        context.dataStore.edit { it[Keys.TEMP_WARNING_THRESHOLD_C] = tempC }
    }

    suspend fun setDataRetentionDays(days: Int) {
        context.dataStore.edit { it[Keys.DATA_RETENTION_DAYS] = days }
    }

    suspend fun setStartOnBootEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.START_ON_BOOT] = enabled }
    }

    private object Defaults {
        // Very rough placeholder model; documented in Phase 4.
        const val DRAIN_COEFFICIENT_MAH_PER_MS: Double = 1e-6
        const val BACKGROUND_ALERT_THRESHOLD_MIN: Int = 60
        const val COLLECTION_INTERVAL_MIN: Int = 15
        const val TEMP_WARNING_THRESHOLD_C: Int = 45
        const val DATA_RETENTION_DAYS: Int = 14
        const val START_ON_BOOT: Boolean = true
    }

    private object Keys {
        val DRAIN_COEFFICIENT_MAH_PER_MS = doublePreferencesKey("drain_coefficient_mah_per_ms")
        val BACKGROUND_ALERT_THRESHOLD_MIN = intPreferencesKey("bg_alert_threshold_min")
        val COLLECTION_INTERVAL_MIN = intPreferencesKey("collection_interval_min")
        val TEMP_WARNING_THRESHOLD_C = intPreferencesKey("temp_warning_threshold_c")
        val DATA_RETENTION_DAYS = intPreferencesKey("data_retention_days")
        val START_ON_BOOT = booleanPreferencesKey("start_on_boot")
    }
}

