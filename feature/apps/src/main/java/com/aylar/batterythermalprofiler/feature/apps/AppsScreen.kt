package com.aylar.batterythermalprofiler.feature.apps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppsScreen(
    state: AppsUiState,
    onWindowChange: (AppsWindow) -> Unit,
    onSortChange: (AppsSort) -> Unit,
    onQueryChange: (String) -> Unit,
    onSelect: (String) -> Unit,
    onDismissSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Apps",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
        )

        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            label = { Text("Search package") },
        )

        Spacer(Modifier.height(12.dp))

        FilterRow(
            window = state.window,
            sort = state.sort,
            onWindowChange = onWindowChange,
            onSortChange = onSortChange,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(12.dp))

        if (state.items.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (state.isLoading) "Loading…" else "No app power data yet.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.items, key = { it.entry.packageName }) { item ->
                    AppRow(
                        item = item,
                        onClick = { onSelect(item.entry.packageName) },
                    )
                }
            }
        }

        if (state.selectedPackageName != null) {
            AppDetailSheet(
                packageName = state.selectedPackageName,
                window = state.window,
                wakelocks = state.selectedWakelocks,
                onDismiss = onDismissSheet,
            )
        }
    }
}

@Composable
private fun FilterRow(
    window: AppsWindow,
    sort: AppsSort,
    onWindowChange: (AppsWindow) -> Unit,
    onSortChange: (AppsSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppsWindow.entries.forEach { w ->
                Chip(
                    text = w.label,
                    selected = w == window,
                    onClick = { onWindowChange(w) },
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(text = "Drain", selected = sort == AppsSort.Drain, onClick = { onSortChange(AppsSort.Drain) })
            Chip(text = "Background", selected = sort == AppsSort.BackgroundTime, onClick = { onSortChange(AppsSort.BackgroundTime) })
            Chip(text = "Wakelocks", selected = sort == AppsSort.Wakelocks, onClick = { onSortChange(AppsSort.Wakelocks) })
        }
    }
}

@Composable
private fun Chip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Surface(
        onClick = onClick,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun AppRow(
    item: AppsListItem,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Surface(onClick = onClick, shape = MaterialTheme.shapes.medium, tonalElevation = 1.dp) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.entry.packageName, style = MaterialTheme.typography.titleSmall)
                Text(text = "%.2f mAh".format(item.entry.estimatedMah), style = MaterialTheme.typography.titleSmall)
            }
            Text(
                text = "FG ${formatMs(item.entry.foregroundMs)} • BG ${formatMs(item.entry.backgroundMs)} • Wakelocks ${item.wakelockCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

