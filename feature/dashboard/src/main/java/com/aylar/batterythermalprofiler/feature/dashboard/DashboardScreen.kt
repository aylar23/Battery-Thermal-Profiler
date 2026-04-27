package com.aylar.batterythermalprofiler.feature.dashboard

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aylar.batterythermalprofiler.core.domain.thermal.ThermalSeverity

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun DashboardScreen(
    state: DashboardUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val hasUsagePermission = UsageStatsPermission.hasPermission(context)

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { if (hasUsagePermission) onRefresh() },
    )

    Surface(modifier = modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(text = "Dashboard", style = MaterialTheme.typography.headlineMedium)
                }

                item {
                    BatteryCard(
                        level = state.battery?.level,
                        tempDeciC = state.battery?.temperatureDeciC,
                    )
                }

                item {
                    StatusRow(
                        thermalSeverity = state.thermalSeverity,
                        voltageMv = state.battery?.voltageMv,
                        chargingStatus = state.battery?.status,
                    )
                }

                if (!hasUsagePermission) {
                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Usage Access needed", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Enable Usage Access to show top draining apps and allow refresh.",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Spacer(Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = {
                                        context.startActivity(
                                            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                        )
                                    },
                                ) {
                                    Text("Open Usage Access Settings")
                                }
                            }
                        }
                    }
                } else {
                    item {
                        SectionCard(
                            title = "Top Draining Apps",
                            emptyText = "No app power data yet. Pull to refresh.",
                            isEmpty = state.topApps.isEmpty(),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.topApps.forEach { e ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(text = e.packageName, style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "%.2f mAh".format(e.estimatedMah), style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    SectionCard(
                        title = "Recent Wakelock Events",
                        emptyText = "No wakelock events captured yet.",
                        isEmpty = state.recentWakelocks.isEmpty(),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            state.recentWakelocks.forEach { e ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(text = e.tag, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = e.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.errorMessage != null) {
                    item {
                        Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
private fun BatteryCard(
    level: Int?,
    tempDeciC: Int?,
    modifier: Modifier = Modifier,
) {
    val progressTarget = ((level ?: 0) / 100f).coerceIn(0f, 1f)
    val progress by animateFloatAsState(progressTarget, label = "batteryArc")
    val tempText = tempDeciC?.let { "${it / 10f}°C" } ?: "—"
    val levelText = level?.let { "$it%" } ?: "—"

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Canvas(modifier = Modifier.size(84.dp)) {
                val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                val size = Size(size.width, size.height)
                drawArc(
                    color = trackColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = stroke,
                    size = size,
                )
                drawArc(
                    color = progressColor,
                    startAngle = 135f,
                    sweepAngle = 270f * progress,
                    useCenter = false,
                    style = stroke,
                    size = size,
                )
            }
            Column {
                Text(text = "Battery", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(text = levelText, style = MaterialTheme.typography.headlineSmall)
                Text(text = "Temp: $tempText", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun StatusRow(
    thermalSeverity: ThermalSeverity,
    voltageMv: Int?,
    chargingStatus: Int?,
    modifier: Modifier = Modifier,
) {
    val chipColor = when (thermalSeverity) {
        ThermalSeverity.None, ThermalSeverity.Light -> Color(0xFF2E7D32)
        ThermalSeverity.Moderate -> Color(0xFFF9A825)
        ThermalSeverity.Severe -> Color(0xFFEF6C00)
        ThermalSeverity.Critical, ThermalSeverity.Emergency, ThermalSeverity.Shutdown -> Color(0xFFC62828)
        ThermalSeverity.Unknown -> MaterialTheme.colorScheme.outline
    }

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Thermal", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = thermalSeverity.name,
                    color = chipColor,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Card(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Voltage", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = voltageMv?.let { "${it} mV" } ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Card(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Status", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = chargingStatus?.toString() ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    emptyText: String,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            if (isEmpty) {
                Text(text = emptyText, style = MaterialTheme.typography.bodyMedium)
            } else {
                content()
            }
        }
    }
}

