package com.jawharat.manifest.domain.entity.manifest


data class Manifest(
    val date: String = "",
    val price: Int? = null,
    val from: String = "",
    val to: String = "",
    val plateNumber: String = "",
    val vehicleType: String = "",
    val driverName: String = "",
    val driverId: String = "",
    val driverPhoneNumber: String = "",
)
