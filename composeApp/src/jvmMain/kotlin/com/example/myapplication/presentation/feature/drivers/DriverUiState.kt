package com.example.myapplication.presentation.feature.drivers

import androidx.compose.runtime.Immutable
import com.example.myapplication.domain.entity.Driver
import com.example.myapplication.presentation.feature.shared.SearchState

@Immutable
data class DriverUiState(
    val drivers: List<Driver> = emptyList(),
    val filteredDrivers: List<Driver> = emptyList(),
    val driverToEdit: Driver? = null,
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val searchState: SearchState<Driver> = SearchState(),
)
