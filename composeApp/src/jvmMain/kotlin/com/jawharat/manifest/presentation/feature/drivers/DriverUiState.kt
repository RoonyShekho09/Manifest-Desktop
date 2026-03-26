package com.jawharat.manifest.presentation.feature.drivers

import androidx.compose.runtime.Immutable
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.presentation.feature.shared.SearchState

@Immutable
data class DriverUiState(
    val drivers: List<Driver> = emptyList(),
    val filteredDrivers: List<Driver> = emptyList(),
    val driverToEdit: Driver? = null,
    val driverToAdd: Driver? = null,
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val searchState: SearchState<Driver> = SearchState(),
)
