package com.jawharat.manifest.presentation.feature.drivers

import androidx.compose.runtime.Immutable
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.presentation.feature.shared.SearchState
import kotlinx.collections.immutable.*

@Immutable
data class DriverUiState(
    val drivers: ImmutableList<Driver> = persistentListOf(),
    val driverToEdit: Driver? = null,
    val driverToAdd: Driver? = null,
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val mainSearchState: SearchState<Driver> = SearchState(),
    val dialogSearchState: SearchState<Driver> = SearchState(),
)
