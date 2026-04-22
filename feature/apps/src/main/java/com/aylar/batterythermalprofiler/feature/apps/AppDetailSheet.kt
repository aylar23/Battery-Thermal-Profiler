package com.aylar.batterythermalprofiler.feature.apps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailSheet(
    packageName: String,
    window: AppsWindow,
    wakelocks: List<com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent> = emptyList(),
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = packageName, style = MaterialTheme.typography.titleLarge)
            Text(text = "Window: ${window.label}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(4.dp))

            Text(text = "Timeline", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Chart placeholder (Vico integration in Phase 8).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(6.dp))

            Text(text = "Wakelocks", style = MaterialTheme.typography.titleMedium)
            if (wakelocks.isEmpty()) {
                Text(
                    text = "No wakelocks for this app in the selected window.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    wakelocks.take(30).forEach { e ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = e.tag, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${e.durationMs} ms",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

