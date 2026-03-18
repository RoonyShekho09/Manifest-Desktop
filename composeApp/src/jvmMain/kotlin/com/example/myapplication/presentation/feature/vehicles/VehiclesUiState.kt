package com.example.myapplication.presentation.feature.vehicles

import androidx.compose.runtime.Immutable
import com.example.myapplication.domain.entity.Vehicle
import com.example.myapplication.presentation.feature.shared.SearchState

@Immutable
data class VehiclesUiState(
    val vehicles: List<VehicleUiState> = emptyList(),
    val vehicleToEdit: VehicleUiState? = null,
    val filteredVehicles: List<VehicleUiState> = emptyList(),
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val searchState: SearchState<VehicleUiState> = SearchState(),
)

data class VehicleUiState(
    val id: String = "",
    val driverName: String = "",
    val plateNumber: String = "",
    val carType: String = "",
    val type: String = "",
    val price: String = "",
    val line: String = "",
    val status: DriverStatus = DriverStatus.INSIDE
)

enum class DriverStatus {
    INSIDE, OUTSIDE
}

fun List<Vehicle>.toUiState() = map { it.toUiState() }

fun Vehicle.toUiState() = VehicleUiState(
    id = id,
    driverName = driverInformation.name,
    plateNumber = vehicleNumber,
    carType = carType,
    type = type,
    price = price.toString(),
    line = line.name,
    status = if (isInside) DriverStatus.INSIDE else DriverStatus.OUTSIDE
)
