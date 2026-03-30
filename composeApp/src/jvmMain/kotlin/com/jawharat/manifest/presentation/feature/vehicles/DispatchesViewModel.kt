package com.jawharat.manifest.presentation.feature.vehicles

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import com.jawharat.manifest.utils.normalizeArabicKurdish
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class DispatchesViewModel(private val repository: ManifestRepository) :
    BaseViewModel<DispatchesUiState, Unit>(DispatchesUiState()) {

    init {
        initializeDispatches()
        initializeLines()
        initializeVehicleTypes()
        initializeDrivers()
        state.value.dispatchSearchState.query.initializeSearch(
            onSearch = ::onSearch,
            onEmptyStateUpdater = { copy(filteredDispatches = dispatches) }
        )
        state.value.vehicleTypeSearchState.query.initializeSearch(
            onSearch = ::onVehicleTypeSearch,
            onEmptyStateUpdater = { copy(filteredVehicleTypes = vehicleTypes) }
        )
        state.value.driverSearchState.query.initializeSearch(
            onSearch = ::onDriverSearch,
            onEmptyStateUpdater = { copy(filteredDrivers = drivers) }
        )
    }

    private fun initializeDrivers(fetch: Boolean = false) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.getDrivers(fetch = fetch) },
        onSuccess = { updateState { copy(drivers = it, filteredDrivers = it) } },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    private fun onDriverSearch(query: String) = updateState {
        val normalizedQuery = query.normalizeArabicKurdish()

        copy(
            filteredDrivers = state.value.drivers.filter {
                it.name.normalizeArabicKurdish().contains(
                    normalizedQuery,
                    ignoreCase = true
                )
            }.sortedBy { it.name }
        )
    }

    private fun onVehicleTypeSearch(query: String) = updateState {
        val normalizedQuery = query.normalizeArabicKurdish()

        copy(
            filteredVehicleTypes = state.value.vehicleTypes.filter {
                it.name.normalizeArabicKurdish().contains(
                    normalizedQuery,
                    ignoreCase = true
                )
            }.sortedBy { it.name }
        )
    }

    private fun initializeVehicleTypes(fetch: Boolean = false) = tryToExecute(
        block = { repository.getVehicleTypes(fetch = fetch) },
        onSuccess = { updateState { copy(vehicleTypes = it) } }
    )

    fun onConfirmAddEditVehicle(value: DispatchUiState?) {
        if (state.value.dispatchToEdit != null)
            editVehicle(value)
        else
            addVehicle(value)
    }

    private fun onSearch(query: String) = updateState {
        copy(
            filteredDispatches = state.value.dispatches.filter {
                it.driverName.contains(
                    query,
                    ignoreCase = true
                ) || it.price.contains(
                    query,
                    ignoreCase = true
                ) || it.line.name.contains(
                    query,
                    ignoreCase = true
                )
            }
                .sortedBy {
                    it.driverName
                }
                .sortedBy {
                    !it.driverName.startsWith(query, ignoreCase = true)
                }
        )
    }

    fun initializeLines(fetch: Boolean = false) = tryToExecute(
        block = { repository.getLines(fetch = fetch) },
        onSuccess = { updateState { copy(lines = it) } }
    )

    fun editVehicle(value: DispatchUiState?) = tryToExecute(
        block = {
            value?.id?.let {
                repository.editVehicle(
                    vehicleNumber = value.plateNumber,
                    type = value.type,
                    carType = value.carType,
                    price = value.price.toIntOrNull(),
                    driverId = value.driverId,
                    line = value.line.id,
                    id = value.id,
                )
            }
        },
        onCompleted = { updateState { copy(isDialogVisible = false) } }
    )

    fun addVehicle(value: DispatchUiState?) = tryToExecute(
        block = {
            value?.id?.let {
                repository.editVehicle(
                    vehicleNumber = value.plateNumber,
                    type = value.type,
                    carType = value.carType,
                    price = value.price.toIntOrNull(),
                    driverId = value.id,
                    line = value.line.id,
                    id = value.id,
                )
            }
        },
        onCompleted = { updateState { copy(isDialogVisible = false) } }
    )

    fun onRefresh() {
        initializeDispatches(fetch = true)
        initializeLines(fetch = true)
        initializeVehicleTypes(fetch = true)
    }

    private fun initializeDispatches(fetch: Boolean = false) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.getDispatches(fetch = fetch) },
        onSuccess = {
            updateState {
                copy(
                    dispatches = it.toUiState(),
                    filteredDispatches = it.toUiState()
                )
            }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onEditClick(id: String? = null) {
        updateState {
            copy(
                isDialogVisible = true,
                dispatchToEdit = state.value.filteredDispatches.firstOrNull { it.id == id }
            )
        }
    }

    fun onQrCodeClick(id: String) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.scanVehicleQrCode(id) },
        onSuccess = {

        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onDismissDialog() = updateState { copy(isDialogVisible = false) }

    @OptIn(FlowPreview::class)
    fun TextFieldState.initializeSearch(
        onSearch: (query: String) -> Unit,
        minQueryLength: Int = 3,
        debounceIntervalMs: Long = 300,
        onEmptyStateUpdater: DispatchesUiState.() -> DispatchesUiState,
    ) = snapshotFlow { this.text.toString() }
        .onEach { if (it.isEmpty()) updateState(updater = onEmptyStateUpdater) }
        .debounce(timeoutMillis = debounceIntervalMs)
        .map(String::trim)
        .filter { it.length >= minQueryLength }
        .distinctUntilChanged()
        .onEach(action = onSearch)
        .launchIn(viewModelScope)
}
