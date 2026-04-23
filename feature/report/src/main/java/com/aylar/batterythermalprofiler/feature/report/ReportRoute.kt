package com.aylar.batterythermalprofiler.feature.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReportRoute(
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    ReportScreen(
        state = state,
        onRangeChange = viewModel::setRange,
        onGenerate = viewModel::generateReport,
    )
}

