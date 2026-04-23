package com.aylar.batterythermalprofiler.feature.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import android.content.Intent

@Composable
fun ReportScreen(
    state: ReportUiState,
    onRangeChange: (ReportRange) -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Report", style = MaterialTheme.typography.headlineMedium)

        RangeRow(range = state.range, onRangeChange = onRangeChange)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onGenerate,
                enabled = !state.isGenerating,
            ) {
                if (state.isGenerating) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                    Spacer(Modifier.height(0.dp))
                } else {
                    Text("Generate")
                }
            }

            OutlinedButton(
                onClick = {
                    val text = state.plainSummary ?: return@OutlinedButton
                    clipboard.setText(AnnotatedString(text))
                },
                enabled = !state.plainSummary.isNullOrBlank(),
            ) {
                Text("Copy summary")
            }

            OutlinedButton(
                onClick = {
                    val uriStr = state.pdfUriString ?: return@OutlinedButton
                    val uri = android.net.Uri.parse(uriStr)
                    val share = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(share, "Share report"))
                },
                enabled = !state.pdfUriString.isNullOrBlank(),
            ) {
                Text("Share PDF")
            }
        }

        if (state.errorMessage != null) {
            Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Summary", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.plainSummary ?: "Generate a report to see a summary here.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "HTML (preview)", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.html?.take(1200) ?: "Generate a report to see HTML here.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun RangeRow(
    range: ReportRange,
    onRangeChange: (ReportRange) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ReportRange.entries.forEach { r ->
            Surface(
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

