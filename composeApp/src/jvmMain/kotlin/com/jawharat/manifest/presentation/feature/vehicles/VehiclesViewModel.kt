package com.jawharat.manifest.presentation.feature.vehicles

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

class VehiclesViewModel(private val repository: ManifestRepository) :
    BaseViewModel<VehiclesUiState, Unit>(VehiclesUiState()) {

    init {
        initializeVehicles()
        initializeLines()
        state.value.searchState.query.initializeSearch(
            onSearch = { query ->
                updateState {
                    copy(
                        filteredVehicles = state.value.vehicles.filter {
                            it.driverName.contains(
                                query,
                                ignoreCase = true
                            ) || it.price.contains(
                                query,
                                ignoreCase = true
                            ) || it.line.contains(
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
            },
            onEmptyStateUpdater = { copy(filteredVehicles = vehicles) }
        )
    }

    fun initializeLines() = tryToExecute(
        block = repository::getLines,
        onSuccess = {

        }
    )

    fun editVehicle() = tryToExecute(
        block = {
            with(state.value.vehicleToEdit) {
                this?.id?.let {
                    repository.editVehicle(
                        vehicleNumber = plateNumber,
                        type = type,
                        carType = carType,
                        price = price.toIntOrNull(),
                        driverId = id,
                        line = line,
                        id = it,
                    )
                }
            }
        }
    )

    fun addVehicle() = tryToExecute(
        block = {
            with(state.value.vehicleToAdd) {
                this?.id?.let {
                    repository.editVehicle(
                        vehicleNumber = plateNumber,
                        type = type,
                        carType = carType,
                        price = price.toIntOrNull(),
                        driverId = id,
                        line = line,
                        id = it,
                    )
                }
            }
        }
    )

    fun onRefresh() = initializeVehicles(fetch = true)

    private fun initializeVehicles(fetch: Boolean = false) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.getVehicles(fetch = fetch) },
        onSuccess = {
            updateState {
                copy(
                    vehicles = it.toUiState(),
                    filteredVehicles = it.toUiState()
                )
            }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onEditClick(id: String) {
        updateState {
            copy(
                isDialogVisible = true,
                vehicleToEdit = state.value.filteredVehicles.firstOrNull { it.id == id }
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
        onEmptyStateUpdater: VehiclesUiState.() -> VehiclesUiState,
    ) = snapshotFlow { this.text.toString() }
        .onEach { if (it.isEmpty()) updateState(updater = onEmptyStateUpdater) }
        .debounce(timeoutMillis = debounceIntervalMs)
        .map(String::trim)
        .filter { it.length >= minQueryLength }
        .distinctUntilChanged()
        .onEach(action = onSearch)
        .launchIn(viewModelScope)
}
