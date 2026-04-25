package com.aylar.batterythermalprofiler.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onCollectionIntervalChange: (Int) -> Unit,
    onBackgroundThresholdChange: (Int) -> Unit,
    onTempThresholdChange: (Int) -> Unit,
    onRetentionDaysChange: (Int) -> Unit,
    onStartOnBootToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        SettingCard(
            title = "Collection interval",
            subtitle = "WorkManager periodic collection (min 15 minutes).",
        ) {
            ChoiceRow(
                selected = state.collectionIntervalMinutes,
                options = listOf(15, 30, 60),
                label = { "${it}m" },
                onSelect = onCollectionIntervalChange,
            )
        }

        SettingCard(
            title = "Background time alert threshold",
            subtitle = "Used to flag apps with high background usage.",
        ) {
            ChoiceRow(
                selected = state.backgroundAlertThresholdMinutes,
                options = listOf(15, 30, 60, 120),
                label = { "${it}m" },
                onSelect = onBackgroundThresholdChange,
            )
        }

        SettingCard(
            title = "Temperature warning threshold",
            subtitle = "Used for future warnings/notifications.",
        ) {
            ChoiceRow(
                selected = state.temperatureWarningThresholdC,
                options = listOf(40, 45, 50, 55),
                label = { "${it}°C" },
                onSelect = onTempThresholdChange,
            )
        }

        SettingCard(
            title = "Data retention",
            subtitle = "Daily pruning deletes older records.",
        ) {
            ChoiceRow(
                selected = state.dataRetentionDays,
                options = listOf(7, 14, 30),
                label = { "${it}d" },
                onSelect = onRetentionDaysChange,
            )
        }

        SettingCard(
            title = "Start monitoring on boot",
            subtitle = "Controls whether monitoring starts after BOOT_COMPLETED.",
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = if (state.startOnBootEnabled) "Enabled" else "Disabled")
                Switch(
                    checked = state.startOnBootEnabled,
                    onCheckedChange = onStartOnBootToggle,
                )
            }
        }

        if (state.errorMessage != null) {
            Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            content()
        }
    }
}

@Composable
private fun ChoiceRow(
    selected: Int,
    options: List<Int>,
    label: (Int) -> String,
    onSelect: (Int) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { v ->
            Surface(
                onClick = { onSelect(v) },
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.large,
            ) {
                Text(
                    text = label(v),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (v == selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

