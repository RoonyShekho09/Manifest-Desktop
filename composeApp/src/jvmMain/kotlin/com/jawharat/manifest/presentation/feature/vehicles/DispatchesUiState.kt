package com.jawharat.manifest.presentation.feature.vehicles

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.Dispatch
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.VehicleType
import com.jawharat.manifest.presentation.feature.shared.SearchState

@Immutable
data class DispatchesUiState(
    val dispatches: List<DispatchUiState> = emptyList(),
    val dispatchToEdit: DispatchUiState? = null,
    val dispatchToAdd: DispatchUiState? = null,
    val filteredDispatches: List<DispatchUiState> = emptyList(),
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val dispatchSearchState: SearchState<DispatchUiState> = SearchState(),
    val lines: List<Line> = emptyList(),
    val vehicleTypes: List<VehicleType> = emptyList(),
    val filteredVehicleTypes: List<VehicleType> = emptyList(),
    val vehicleTypeSearchState: SearchState<VehicleType> = SearchState(),
    val filteredDrivers: List<Driver> = emptyList(),
    val driverSearchState: SearchState<Driver> = SearchState(),
    val drivers: List<Driver> = emptyList(),
)

@Stable
data class DispatchUiState(
    val id: String = "",
    val driverId: String = "",
    val driverName: String = "",
    val plateNumber: String = "",
    val carType: String = "",
    val type: String = "",
    val price: String = "",
    val line: Line = Line("", ""),
    val status: DriverStatus = DriverStatus.INSIDE,
)

enum class DriverStatus {
    INSIDE, OUTSIDE
}

fun List<Dispatch>.toUiState() = map { it.toUiState() }

fun Dispatch.toUiState() = DispatchUiState(
    id = id,
    driverId = driverInformation._id,
    driverName = driverInformation.name,
    plateNumber = vehicleNumber,
    carType = vehicleType,
    type = type,
    price = price.toString(),
    line = line,
    status = if (isInside) DriverStatus.INSIDE else DriverStatus.OUTSIDE
)
