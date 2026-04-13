package com.jawharat.manifest.presentation.feature.vehicles

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import com.jawharat.manifest.utils.normalizeArabicKurdish
import kotlinx.collections.immutable.toImmutableList
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
        initializePrice()
        state.value.dispatchSearchState.query.initializeSearch(
            onSearch = ::onSearch,
            onEmptyStateUpdater = {
                copy(
                    dispatchSearchState = dispatchSearchState.copy(
                        searchResults = dispatches.toImmutableList()
                    )
                )
            }
        )
        state.value.vehicleTypeSearchState.query.initializeSearch(
            onSearch = ::onVehicleTypeSearch,
            onEmptyStateUpdater = {
                copy(
                    vehicleTypeSearchState = vehicleTypeSearchState.copy(
                        searchResults = carTypes.toImmutableList()
                    )
                )
            }
        )
        state.value.driverSearchState.query.initializeSearch(
            onSearch = ::onDriverSearch,
            onEmptyStateUpdater = { copy(driverSearchState = driverSearchState.copy(searchResults = drivers.toImmutableList())) }
        )
    }

    private fun initializePrice() = tryToExecute(
        block = { repository.getPrice("693d62bb417d7b42b11e7987") },
        onSuccess = { updateState { copy(price = it) } }
    )

    private fun initializeDrivers(fetch: Boolean = false) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.getDrivers(fetch = fetch) },
        onSuccess = {
            updateState {
                copy(
                    drivers = it,
                    driverSearchState = driverSearchState.copy(searchResults = it.toImmutableList())
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
                ).toImmutableList()
            )
        )
    }

    private fun onVehicleTypeSearch(query: String) = updateState {
        val normalizedQuery = query.normalizeArabicKurdish()

        copy(
            vehicleTypeSearchState = vehicleTypeSearchState.copy(
                searchResults = state.value.carTypes.filter {
                    it.name.normalizeArabicKurdish().contains(
                        normalizedQuery,
                        ignoreCase = true
                    )
                }.sortedBy { it.name }
                    .toImmutableList()
            )
        )
    }

    private fun initializeVehicleTypes(fetch: Boolean = false) = tryToExecute(
        block = { repository.getVehicleTypes(fetch = fetch) },
        onSuccess = { updateState { copy(carTypes = it) } }
    )

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
                    it.driver.name.contains(
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
                        it.driver.name
                    }
                    .sortedBy {
                        !it.driver.name.startsWith(query, ignoreCase = true)
                    }
                    .toImmutableList()
            )
        )
    }

    fun initializeLines(fetch: Boolean = false) = tryToExecute(
        block = { repository.getLines(fetch = fetch) },
        onSuccess = { updateState { copy(dispatchLines = it) } }
    )

    fun editDispatch(value: DispatchUiState?) = tryToExecute(
        block = {
            value?.id?.let {
                repository.editVehicle(
                    vehicleNumber = value.plateNumber.trimEnd(),
                    vehicleName = value.vehicleName.trimEnd(),
                    vehicleType = value.vehicleType.trimEnd(),
                    price = value.price.toIntOrNull(),
                    driverId = value.driver.id,
                    line = value.line.id,
                    id = value.id,
                )
            }
        },
        onSuccess = { initializeDispatches(fetch = true) },
        onCompleted = { updateState { copy(isDialogVisible = false) } }
    )

    fun addDispatch(value: DispatchUiState?) = tryToExecute(
        block = {
            value?.id?.let {
                repository.addVehicle(
                    plateNumber = value.plateNumber.trimEnd(),
                    vehicleName = value.vehicleName.trimEnd(),
                    price = value.price.toIntOrNull(),
                    driverId = value.driver.id,
                    line = value.line.id,
                    vehicleType = value.vehicleType.trimEnd()
                )
            }
        },
        onSuccess = { initializeDispatches(fetch = true) },
        onCompleted = { updateState { copy(isDialogVisible = false) } }
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
                    dispatchSearchState = dispatchSearchState.copy(
                        searchResults = it.toUiState().toImmutableList()
                    )
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

    fun onDismissDialog() = updateState { copy(isDialogVisible = false, dispatchToEdit = null) }

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
