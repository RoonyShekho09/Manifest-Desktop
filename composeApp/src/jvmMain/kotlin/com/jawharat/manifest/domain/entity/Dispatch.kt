package com.jawharat.manifest.domain.entity

data class Dispatch(
    val vehicleName: String,
    val driverInformation: DriverInformation,
    val id: String,
    val isInside: Boolean,
    val dispatchLine: DispatchLine,
    val office: Office,
    val price: Int,
    val vehicleType: String,
    val vehicleNumber: String
)

data class Office(
    val id: String,
    val name: String
)

data class DriverInformation(
    val destination: String,
    val _id: String,
    val id: String,
    val name: String,
    val phoneNumber: String
)
