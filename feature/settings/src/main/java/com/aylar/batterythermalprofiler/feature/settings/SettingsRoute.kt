package com.aylar.batterythermalprofiler.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    SettingsScreen(
        state = state,
        onCollectionIntervalChange = viewModel::setCollectionIntervalMinutes,
        onBackgroundThresholdChange = viewModel::setBackgroundAlertThresholdMinutes,
        onTempThresholdChange = viewModel::setTemperatureWarningThresholdC,
        onRetentionDaysChange = viewModel::setDataRetentionDays,
        onStartOnBootToggle = viewModel::setStartOnBootEnabled,
    )
}

