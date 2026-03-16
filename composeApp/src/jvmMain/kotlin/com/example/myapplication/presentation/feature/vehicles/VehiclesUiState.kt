package com.example.myapplication.presentation.feature.vehicles

import androidx.compose.runtime.Immutable
import com.example.myapplication.domain.entity.Vehicle

@Immutable
data class VehiclesUiState(
    val cars: List<VehicleUiState> = emptyList(),
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
)

enum class DriverStatus {
    INSIDE, OUTSIDE
}

data class VehicleUiState(
    val id: String,
    val driverName: String,
    val plateNumber: String,
    val carType: String,
    val type: String,
    val price: String,
    val line: String,
    val status: DriverStatus
)

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
