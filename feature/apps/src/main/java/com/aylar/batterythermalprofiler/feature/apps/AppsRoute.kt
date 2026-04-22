package com.aylar.batterythermalprofiler.feature.apps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppsRoute(
    viewModel: AppsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AppsScreen(
        state = state,
        onWindowChange = viewModel::setWindow,
        onSortChange = viewModel::setSort,
        onQueryChange = viewModel::setQuery,
        onSelect = viewModel::select,
        onDismissSheet = viewModel::clearSelection,
    )
}

