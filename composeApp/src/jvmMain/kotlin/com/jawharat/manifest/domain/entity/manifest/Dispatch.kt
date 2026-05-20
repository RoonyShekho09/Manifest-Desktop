package com.jawharat.manifest.domain.entity.manifest

data class Dispatch(
    val id: String,
    val vehicleName: String,
    val driverInformation: DriverInformation,
    val isInside: Boolean,
    val dispatchLine: DispatchLine,
    val office: Office,
    val price: Int,
    val vehicleType: String,
    val plateNumber: String,
    val blocked: Boolean,
)

data class DispatchSummary(
    val id: String,
    val driverId: String,
    val vehicleName: String,
    val vehicleType: String,
    val plateNumber: String,
    val isInside: Boolean,
    val line: String,
    val price: Int,
)

data class DispatchQrResult(
    val line: String,
    val price: Int,
    val plateNumber: String,
    val vehicleName: String,
)

data class Office(
    val id: String = "",
    val name: String = ""
)

data class DriverInformation(
    val destination: String = "",
    val _id: String = "",
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = ""
)
