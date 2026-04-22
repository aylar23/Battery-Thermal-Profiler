package com.aylar.batterythermalprofiler.feature.apps

import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent

enum class AppsSort {
    Drain,
    BackgroundTime,
    Wakelocks,
}

enum class AppsWindow(val durationMillis: Long, val label: String) {
    Last1h(60L * 60L * 1000L, "1h"),
    Last6h(6L * 60L * 60L * 1000L, "6h"),
    Last24h(24L * 60L * 60L * 1000L, "24h"),
}

data class AppsListItem(
    val entry: AppPowerEntry,
    val wakelockCount: Int,
)

data class AppsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val window: AppsWindow = AppsWindow.Last1h,
    val sort: AppsSort = AppsSort.Drain,
    val query: String = "",
    val items: List<AppsListItem> = emptyList(),
    val selectedPackageName: String? = null,
    val selectedWakelocks: List<WakelockEvent> = emptyList(),
)

