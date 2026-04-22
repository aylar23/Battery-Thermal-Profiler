package com.aylar.batterythermalprofiler.feature.trends

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TrendsRoute(
    viewModel: TrendsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    TrendsScreen(
        state = state,
        onRangeChange = viewModel::setRange,
    )
}

