package com.rezz.obatcheck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rezz.obatcheck.data.Category
import com.rezz.obatcheck.data.CheckStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Filter { SEMUA, SUDAH, BELUM }

data class ObatUiState(
    val categories: List<Category> = emptyList(),
    val checkedIds: Set<String> = emptySet(),
    val query: String = "",
    val filter: Filter = Filter.SEMUA,
    val showResetDialog: Boolean = false,
) {
    val total: Int get() = categories.sumOf { it.drugs.size }

    val checkedCount: Int get() =
        categories.sumOf { cat -> cat.drugs.count { it.id in checkedIds } }

    /** Kategori yang masih punya obat setelah pencarian + filter diterapkan. */
    fun visibleCategories(): List<Category> {
        val q = query.trim().lowercase()
        return categories.mapNotNull { cat ->
            val drugs = cat.drugs.filter { drug ->
                (q.isEmpty() || drug.name.lowercase().contains(q)) &&
                    when (filter) {
                        Filter.SEMUA -> true
                        Filter.SUDAH -> drug.id in checkedIds
                        Filter.BELUM -> drug.id !in checkedIds
                    }
            }
            if (drugs.isEmpty()) null else cat.copy(drugs = drugs)
        }
    }
}

class ObatViewModel(
    private val checkStore: CheckStore,
    categories: List<Category>,
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val filterFlow = MutableStateFlow(Filter.SEMUA)
    private val resetDialogFlow = MutableStateFlow(false)

    val state: StateFlow<ObatUiState> = combine(
        checkStore.checkedIds,
        queryFlow,
        filterFlow,
        resetDialogFlow,
    ) { checked, query, filter, showReset ->
        ObatUiState(categories, checked, query, filter, showReset)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ObatUiState(categories),
    )

    fun onQueryChange(query: String) {
        queryFlow.value = query
    }

    fun onFilterChange(filter: Filter) {
        filterFlow.value = filter
    }

    fun toggleDrug(id: String, currentlyChecked: Boolean) {
        viewModelScope.launch { checkStore.setChecked(id, !currentlyChecked) }
    }

    fun showResetDialog(show: Boolean) {
        resetDialogFlow.value = show
    }

    fun resetAll() {
        viewModelScope.launch { checkStore.resetAll() }
        resetDialogFlow.value = false
    }
}
