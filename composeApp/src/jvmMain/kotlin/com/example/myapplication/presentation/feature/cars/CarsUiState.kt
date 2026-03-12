package com.example.myapplication.presentation.feature.cars

import androidx.compose.runtime.Immutable

@Immutable
data class CarsUiState(
    val cars: List<Car> = listOf(
        Car(
            "DRV-9921", "Sarah Connor", "+1 555-0102", "Central Warehouse",
            type = "Type",
            price = "1000",
            lineFrom = "Erbil",
            lineTo = "Baghdad",
            status = DriverStatus.OUTSIDE
        ),
        Car(
            "DRV-9921", "Sarah Connor", "+1 555-0102", "Central Warehouse",
            type = "Type",
            price = "1000",
            lineFrom = "Erbil",
            lineTo = "Baghdad",
            status = DriverStatus.OUTSIDE
        ),
        Car(
            "DRV-9921", "Sarah Connor", "+1 555-0102", "Central Warehouse",
            type = "Type",
            price = "1000",
            lineFrom = "Erbil",
            lineTo = "Baghdad",
            status = DriverStatus.OUTSIDE
        )
    ),
    val isDialogVisible: Boolean = false,
)

enum class DriverStatus {
    INSIDE, OUTSIDE
}

data class Car(
    val id: String,
    val driverName: String,
    val plateNumber: String,
    val carType: String,
    val type: String,
    val price: String,
    val lineFrom: String,
    val lineTo: String,
    val status: DriverStatus
)
