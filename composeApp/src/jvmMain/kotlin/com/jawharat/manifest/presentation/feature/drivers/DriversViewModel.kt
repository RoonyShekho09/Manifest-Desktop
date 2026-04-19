package com.jawharat.manifest.presentation.feature.drivers

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import com.jawharat.manifest.presentation.feature.shared.AppSnackBarHostState
import com.jawharat.manifest.presentation.feature.shared.SearchState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.failed_to_add_driver
import com.jawharat.manifest.utils.generateQRCode
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.collections.filter
import kotlin.uuid.ExperimentalUuidApi

class DriversViewModel(
    private val repository: ManifestRepository,
    private val snackBarHostState: AppSnackBarHostState,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseViewModel<DriverUiState, Unit>(DriverUiState(), ioDispatcher = ioDispatcher) {

    init {
        initializeDrivers()
        //   getLines()
        state.value.mainSearchState.query.initializeSearch(
            onSearch = ::onMainScreenSearch,
            onEmptyStateUpdater = { copy(mainSearchState = mainSearchState.copy(searchResults = drivers)) }
        )
    }

    fun onConfirmAddEditDriver(value: Driver?, isEdit: Boolean) {
        if (isEdit)
            editDriver(value)
        else
            addDriver(value)
    }

    fun getLines() = tryToExecute(
        block = repository::getLines,
        onSuccess = {

        }
    )

    @OptIn(ExperimentalUuidApi::class)
    fun addDriver(value: Driver?) = tryToExecute(
        block = {
            with(value) {
                this?.id?.let {
                    repository.addDriver(
                        driverId = driverId,
                        name = name.trimEnd(),
                        phoneNumber = phone.trimEnd(),
                        destination = destination.trimEnd(),
                    )
                }
            }
        },
        onSuccess = {
            initializeDrivers(fetch = true)
            updateState { copy(mainSearchState = SearchState()) }
        },
        onError = {
            snackBarHostState.showFailure(Res.string.failed_to_add_driver)
        }
    )

    fun editDriver(value: Driver?) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = {
            with(value) {
                this?.id?.let {
                    repository.editDriver(
                        driverId = driverId,
                        name = name.trimEnd(),
                        phoneNumber = phone.trimEnd(),
                        destination = destination.trimEnd(),
                        id = id,
                    )
                }
            }
        },
        onSuccess = { initializeDrivers(fetch = true) },
        onCompleted = { updateState { copy(isLoading = false, isDialogVisible = false) } },
    )

    private fun onMainScreenSearch(query: String) =
        updateState {
            copy(
                mainSearchState = mainSearchState.copy(
                    searchResults = state.value.drivers.filter {
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
                        .toImmutableList()
                )
            )
        }

    fun onRefresh() {
        updateState { copy(mainSearchState = mainSearchState.copy(query = TextFieldState())) }
        initializeDrivers(fetch = true)
    }

    private fun initializeDrivers(fetch: Boolean = true) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { repository.getDrivers(fetch = fetch) },
        onSuccess = {
            updateState {
                copy(
                    drivers = if (it.isEmpty()) drivers else it.toImmutableList(),
                    mainSearchState = if (it.isEmpty()) mainSearchState else mainSearchState.copy(
                        searchResults = it.toImmutableList()
                    ),
                )
            }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onGenerateQrCodeClick(id: String) = generateQRCode("D:$id")

    fun onEditClick(id: String? = null) = updateState {
        copy(
            isDialogVisible = true,
            driverToEdit = mainSearchState.searchResults.firstOrNull { it.id == id }
        )
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
