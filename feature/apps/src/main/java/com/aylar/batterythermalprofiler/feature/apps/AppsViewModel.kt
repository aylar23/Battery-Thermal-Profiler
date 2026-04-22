package com.aylar.batterythermalprofiler.feature.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aylar.batterythermalprofiler.core.domain.repository.AppPowerRepository
import com.aylar.batterythermalprofiler.core.domain.repository.WakelockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppsViewModel @Inject constructor(
    private val appPowerRepository: AppPowerRepository,
    private val wakelockRepository: WakelockRepository,
) : ViewModel() {

    private val window = MutableStateFlow(AppsWindow.Last1h)
    private val sort = MutableStateFlow(AppsSort.Drain)
    private val query = MutableStateFlow("")
    private val selectedPackageName = MutableStateFlow<String?>(null)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val entriesForWindow = window.flatMapLatest { w ->
        appPowerRepository.entriesForLatestWindowOfDuration(w.durationMillis)
    }

    private val recentWakelocks = wakelockRepository.recent(limit = 500)

    private val selectedWakelocks = combine(
        selectedPackageName,
        window,
    ) { pkg, w ->
        pkg to w
    }.flatMapLatest { (pkg, w) ->
        if (pkg == null) {
            flowOf(emptyList())
        } else {
            val end = System.currentTimeMillis()
            val start = end - w.durationMillis
            wakelockRepository.eventsBetweenForPackage(pkg, start, end)
        }
    }

    val uiState: StateFlow<AppsUiState> =
        combine(
            window,
            sort,
            query,
            selectedPackageName,
        ) { w, s, q, selected ->
            Quad(w, s, q, selected)
        }
            .combine(errorMessage) { q, err -> Pair(q, err) }
            .combine(entriesForWindow) { (q, err), entries -> Triple(q, err, entries) }
            .combine(recentWakelocks) { (q, err, entries), wakelocks -> Quad2(q, err, entries, wakelocks) }
            .combine(selectedWakelocks) { q2, selectedWl ->
                val base = q2.q
                val w = base.a
                val s = base.b
                val query = base.c
                val selected = base.d
                val err = q2.err
                val entries = q2.entries
                val wakelocks = q2.wakelocks

                val counts = wakelocks.groupingBy { it.packageName }.eachCount()
                var items = entries.map { e ->
                    AppsListItem(entry = e, wakelockCount = counts[e.packageName] ?: 0)
                }

                if (query.isNotBlank()) {
                    val needle = query.trim().lowercase()
                    items = items.filter { it.entry.packageName.lowercase().contains(needle) }
                }

                items = when (s) {
                    AppsSort.Drain -> items.sortedByDescending { it.entry.estimatedMah }
                    AppsSort.BackgroundTime -> items.sortedByDescending { it.entry.backgroundMs }
                    AppsSort.Wakelocks -> items.sortedByDescending { it.wakelockCount }
                }

                AppsUiState(
                    isLoading = false,
                    errorMessage = err,
                    window = w,
                    sort = s,
                    query = query,
                    items = items,
                    selectedPackageName = selected,
                    selectedWakelocks = selectedWl,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppsUiState(isLoading = true),
            )

    fun setWindow(w: AppsWindow) = window.update { w }
    fun setSort(s: AppsSort) = sort.update { s }
    fun setQuery(q: String) = query.update { q }

    fun select(packageName: String) = selectedPackageName.update { packageName }
    fun clearSelection() = selectedPackageName.update { null }
}

private data class Quad<A, B, C, D>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
)

private data class Quad2<Q, E, EN, W>(
    val q: Q,
    val err: E,
    val entries: EN,
    val wakelocks: W,
)

