package com.aylar.batterythermalprofiler.feature.settings

data class SettingsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val collectionIntervalMinutes: Int = 15,
    val backgroundAlertThresholdMinutes: Int = 60,
    val temperatureWarningThresholdC: Int = 45,
    val dataRetentionDays: Int = 14,
    val startOnBootEnabled: Boolean = true,
)

