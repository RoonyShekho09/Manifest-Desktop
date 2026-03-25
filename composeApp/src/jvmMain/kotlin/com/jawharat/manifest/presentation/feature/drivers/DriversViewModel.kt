package com.jawharat.manifest.presentation.feature.drivers

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.collections.filter

class DriversViewModel(private val repository: ManifestRepository) :
    BaseViewModel<DriverUiState, Unit>(DriverUiState()) {

    init {
        initializeDrivers()
        state.value.searchState.query.initializeSearch(
            onSearch = { query ->
                updateState {
                    copy(
                        filteredDrivers = state.value.drivers.filter {
                            it.name.contains(query, ignoreCase = true) || it.destination.contains(
                                query,
                                ignoreCase = true
                            ) || it.phone.contains(
                                query,
                                ignoreCase = true
                            )
                        }
                            .sortedBy {
                                it.name
                            }
                            .sortedBy {
                                !it.name.startsWith(query, ignoreCase = true)
                            }
                    )
                }
            },
            onEmptyStateUpdater = { copy(filteredDrivers = drivers) }
        )
    }

    fun onRefresh() = initializeDrivers(fetch = true)

    private fun initializeDrivers(fetch: Boolean = false) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.getDrivers(fetch = fetch) },
        onSuccess = { updateState { copy(drivers = it, filteredDrivers = it) } },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onEditClick(id: String) {
        updateState {
            copy(
                isDialogVisible = true,
                driverToEdit = state.value.filteredDrivers.firstOrNull { it.id == id }
            )
        }
    }

    fun onDismissDialog() = updateState { copy(isDialogVisible = false) }

    @OptIn(FlowPreview::class)
    fun TextFieldState.initializeSearch(
        onSearch: (query: String) -> Unit,
        minQueryLength: Int = 3,
        debounceIntervalMs: Long = 300,
        onEmptyStateUpdater: DriverUiState.() -> DriverUiState,
    ) = snapshotFlow { this.text.toString() }
        .onEach { if (it.isEmpty()) updateState(updater = onEmptyStateUpdater) }
        .debounce(timeoutMillis = debounceIntervalMs)
        .map(String::trim)
        .filter { it.length >= minQueryLength }
        .distinctUntilChanged()
        .onEach(action = onSearch)
        .launchIn(viewModelScope)
}
