package com.jawharat.manifest.presentation.feature.vehicles

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.jawharat.manifest.domain.entity.Vehicle
import com.jawharat.manifest.presentation.feature.shared.SearchState
import java.util.UUID

@Immutable
data class VehiclesUiState(
    val vehicles: List<VehicleUiState> = emptyList(),
    val vehicleToEdit: VehicleUiState? = null,
    val filteredVehicles: List<VehicleUiState> = emptyList(),
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val searchState: SearchState<VehicleUiState> = SearchState(),
)

@Stable
data class VehicleUiState(
    val id: String = UUID.randomUUID().toString(),
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
