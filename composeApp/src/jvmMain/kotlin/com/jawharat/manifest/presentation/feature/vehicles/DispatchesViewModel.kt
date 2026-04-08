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
            onEmptyStateUpdater = {
                copy(
                    dispatchSearchState = dispatchSearchState.copy(
                        searchResults = dispatches
                    )
                )
            }
        )
        state.value.vehicleTypeSearchState.query.initializeSearch(
            onSearch = ::onVehicleTypeSearch,
            onEmptyStateUpdater = {
                copy(
                    vehicleTypeSearchState = vehicleTypeSearchState.copy(
                        searchResults = vehicleTypes
                    )
                )
            }
        )
        state.value.driverSearchState.query.initializeSearch(
            onSearch = ::onDriverSearch,
            onEmptyStateUpdater = { copy(driverSearchState = driverSearchState.copy(searchResults = drivers)) }
        )
    }

    private fun initializeDrivers(fetch: Boolean = false) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.getDrivers(fetch = fetch) },
        onSuccess = {
            updateState {
                copy(
                    drivers = it,
                    driverSearchState = driverSearchState.copy(searchResults = it)
                )
            }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    private fun onDriverSearch(query: String) = updateState {
        val normalizedQuery = query.normalizeArabicKurdish()

        copy(
            driverSearchState = driverSearchState.copy(
                searchResults = state.value.drivers.filter {
                    it.name.normalizeArabicKurdish().contains(normalizedQuery, ignoreCase = true)
                }.sortedWith(
                    compareBy(
                        { !it.name.startsWith(query, ignoreCase = true) },
                        {
                            !it.name.split(" ").first()
                                .startsWith(normalizedQuery, ignoreCase = true)
                        },
                        {
                            !it.name.split(" ").first().contains(normalizedQuery, ignoreCase = true)
                        },
                        {
                            !it.name.split(" ").getOrElse(1) { "" }
                                .startsWith(normalizedQuery, ignoreCase = true)
                        },
                        {
                            !it.name.split(" ").getOrElse(2) { "" }
                                .startsWith(normalizedQuery, ignoreCase = true)
                        }
                    )
                )
            )
        )
    }

    private fun onVehicleTypeSearch(query: String) = updateState {
        val normalizedQuery = query.normalizeArabicKurdish()

        copy(
            vehicleTypeSearchState = vehicleTypeSearchState.copy(
                searchResults = state.value.vehicleTypes.filter {
                    it.name.normalizeArabicKurdish().contains(
                        normalizedQuery,
                        ignoreCase = true
                    )
                }.sortedBy { it.name }
            )
        )
    }

    private fun initializeVehicleTypes(fetch: Boolean = false) = tryToExecute(
        block = { repository.getVehicleTypes(fetch = fetch) },
        onSuccess = { updateState { copy(vehicleTypes = it) } }
    )

    fun onDriverSearchQueryChange(value: String) =
        updateState { copy(driverSearchState = driverSearchState.copy(query = value)) }

    fun onVehicleTypeSearchQueryChange(value: String) =
        updateState { copy(vehicleTypeSearchState = vehicleTypeSearchState.copy(query = value)) }

    fun onDispatchSearchStateChange(value: String) =
        updateState { copy(dispatchSearchState = dispatchSearchState.copy(query = value)) }

    fun onConfirmAddEditDispatch(value: DispatchUiState?) {
        if (state.value.dispatchToEdit != null)
            editDispatch(value)
        else
            addDispatch(value)
    }

    private fun onSearch(query: String) = updateState {
        copy(
            dispatchSearchState = dispatchSearchState.copy(
                searchResults = state.value.dispatches.filter {
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
        )
    }

    fun initializeLines(fetch: Boolean = false) = tryToExecute(
        block = { repository.getLines(fetch = fetch) },
        onSuccess = { updateState { copy(lines = it) } }
    )

    fun editDispatch(value: DispatchUiState?) = tryToExecute(
        block = {
            value?.id?.let {
                repository.editVehicle(
                    vehicleNumber = value.plateNumber,
                    type = value.type,
                    vehicleType = value.vehicleType,
                    price = value.price.toIntOrNull(),
                    driverId = value.driverId,
                    line = value.line.id,
                    id = value.id,
                )
            }
        },
        onCompleted = {
            initializeDispatches(fetch = true)
            updateState { copy(isDialogVisible = false) }
        }
    )

    fun addDispatch(value: DispatchUiState?) = tryToExecute(
        block = {
            value?.id?.let {
                repository.editVehicle(
                    vehicleNumber = value.plateNumber,
                    type = value.type,
                    vehicleType = value.vehicleType,
                    price = value.price.toIntOrNull(),
                    driverId = value.id,
                    line = value.line.id,
                    id = value.id,
                )
            }
        },
        onCompleted = {
            initializeDispatches(fetch = true)
            updateState { copy(isDialogVisible = false) }
        }
    )

    fun onRefresh() {
        initializeDispatches(fetch = true)
        initializeLines(fetch = true)
        initializeVehicleTypes(fetch = true)
        initializeDrivers(fetch = true)
    }

    private fun initializeDispatches(fetch: Boolean = false) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.getDispatches(fetch = fetch) },
        onSuccess = {
            updateState {
                copy(
                    dispatches = it.toUiState(),
                    dispatchSearchState = dispatchSearchState.copy(searchResults = it.toUiState())
                )
            }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onEditClick(id: String? = null) {
        updateState {
            copy(
                isDialogVisible = true,
                dispatchToEdit = state.value.dispatchSearchState.searchResults.firstOrNull { it.id == id }
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
    fun String.initializeSearch(
        onSearch: (query: String) -> Unit,
        minQueryLength: Int = 3,
        debounceIntervalMs: Long = 300,
        onEmptyStateUpdater: DispatchesUiState.() -> DispatchesUiState,
    ) = snapshotFlow { this }
        .onEach { if (it.isEmpty()) updateState(updater = onEmptyStateUpdater) }
        .debounce(timeoutMillis = debounceIntervalMs)
        .map(String::trim)
        .filter { it.length >= minQueryLength }
        .distinctUntilChanged()
        .onEach(action = onSearch)
        .launchIn(viewModelScope)
}
