package com.jawharat.manifest.presentation.feature.vehicles

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.jawharat.manifest.domain.entity.manifest.DispatchLine
import com.jawharat.manifest.domain.entity.manifest.Dispatch
import com.jawharat.manifest.domain.entity.manifest.Driver
import com.jawharat.manifest.domain.entity.Route
import com.jawharat.manifest.domain.entity.manifest.VehicleType
import com.jawharat.manifest.presentation.feature.shared.SearchState

@Immutable
data class DispatchesUiState(
    val dispatches: List<DispatchUiState> = emptyList(),
    val dispatchToEdit: DispatchUiState? = null,
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val dispatchSearchState: SearchState<DispatchUiState> = SearchState(),
    val dispatchLines: List<DispatchLine> = emptyList(),
    val carTypes: List<VehicleType> = emptyList(),
    val vehicleTypeSearchState: SearchState<VehicleType> = SearchState(),
    val driverSearchState: SearchState<Driver> = SearchState(),
    val drivers: List<Driver> = emptyList(),
    val price: List<Route>? = null
)

@Stable
data class DispatchUiState(
    val id: String = "",
    val driver: DispatchData = DispatchData(),
    val plateNumber: String = "",
    val vehicleName: String = "",
    val vehicleType: String = "",
    val price: String = "",
    val line: DispatchData = DispatchData(),
    val status: DriverStatus = DriverStatus.INSIDE,
    val blocked: Boolean = false,
)

data class DispatchData(
    val id: String = "",
    val name: String = ""
)

enum class DriverStatus {
    INSIDE, OUTSIDE
}

fun List<Dispatch>.toUiState() = map { it.toUiState() }

fun Dispatch.toUiState(): DispatchUiState = DispatchUiState(
    id = id,
    driver = DispatchData(
        id = driverInformation.id,
        name = driverInformation.name,
    ),
    plateNumber = plateNumber,
    vehicleName = vehicleName,
    vehicleType = vehicleType,
    price = price.toString(),
    line = DispatchData(id = dispatchLine.id, name = dispatchLine.name),
    status = if (isInside) DriverStatus.INSIDE else DriverStatus.OUTSIDE,
    blocked = blocked
)
