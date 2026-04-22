@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")

package com.aylar.batterythermalprofiler.feature.trends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
fun TrendsScreen(
    state: TrendsUiState,
    onRangeChange: (TrendsRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Trends", style = MaterialTheme.typography.headlineMedium)

        RangeRow(range = state.range, onRangeChange = onRangeChange)

        if (state.avgDrainPerHour != null) {
            Text(
                text = "Avg drain: %.2f %%/h".format(state.avgDrainPerHour),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        ChartCard(title = "Battery % over time", points = state.batteryPoints)
        ChartCard(title = "Temperature (°C) over time", points = state.tempPoints)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Thermal zone time", style = MaterialTheme.typography.titleMedium)
                if (state.thermalZoneTimes.isEmpty()) {
                    Text(
                        text = "No thermal history yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    state.thermalZoneTimes.forEach { z ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = z.severity.name, style = MaterialTheme.typography.bodyMedium)
                            Text(text = formatDuration(z.durationMillis), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        if (state.errorMessage != null) {
            Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun RangeRow(
    range: TrendsRange,
    onRangeChange: (TrendsRange) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TrendsRange.entries.forEach { r ->
            androidx.compose.material3.Surface(
                onClick = { onRangeChange(r) },
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.large,
            ) {
                Text(
                    text = r.label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (r == range) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    points: List<TrendsPoint>,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(points) {
        modelProducer.runTransaction {
            lineSeries {
                series(points.map { it.y })
            }
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            if (points.size < 2) {
                Text(
                    text = "Not enough data yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                )
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

