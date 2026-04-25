package com.aylar.batterythermalprofiler.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aylar.batterythermalprofiler.core.data.settings.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStore,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> =
        combine(
            settingsStore.collectionIntervalMinutes(),
            settingsStore.backgroundAlertThresholdMinutes(),
            settingsStore.temperatureWarningThresholdC(),
            settingsStore.dataRetentionDays(),
            settingsStore.startOnBootEnabled(),
        ) { interval, bg, temp, retention, boot ->
            SettingsUiState(
                isLoading = false,
                collectionIntervalMinutes = interval,
                backgroundAlertThresholdMinutes = bg,
                temperatureWarningThresholdC = temp,
                dataRetentionDays = retention,
                startOnBootEnabled = boot,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun setCollectionIntervalMinutes(minutes: Int) {
        viewModelScope.launch {
            settingsStore.setCollectionIntervalMinutes(minutes)
        }
    }

    fun setBackgroundAlertThresholdMinutes(minutes: Int) {
        viewModelScope.launch { settingsStore.setBackgroundAlertThresholdMinutes(minutes) }
    }

    fun setTemperatureWarningThresholdC(tempC: Int) {
        viewModelScope.launch { settingsStore.setTemperatureWarningThresholdC(tempC) }
    }

    fun setDataRetentionDays(days: Int) {
        viewModelScope.launch {
            settingsStore.setDataRetentionDays(days)
        }
    }

    fun setStartOnBootEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsStore.setStartOnBootEnabled(enabled) }
    }
}

