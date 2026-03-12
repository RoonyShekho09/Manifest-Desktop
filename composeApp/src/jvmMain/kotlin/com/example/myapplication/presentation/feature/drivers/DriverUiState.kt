package com.example.myapplication.presentation.feature.drivers

import androidx.compose.runtime.Immutable

@Immutable
data class DriverUiState(
    val drivers: List<Driver> = listOf(
        Driver("DRV-9921", "Sarah Connor", "+1 555-0102", "Central Warehouse"),
        Driver("DRV-4432", "James Holden", "+1 555-0193", "Port Terminal B"),
        Driver("DRV-1029", "Ellen Ripley", "+1 555-0144", "Distribution Center Alpha")
    ),
    val isDialogVisible: Boolean = false,
)

data class Driver(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val destination: String = "",
)
