package com.jawharat.manifest.domain.entity

data class Driver(
    val id: String,
    val name: String,
    val phone: String,
    val destination: String,
    val driverId: String,
    val blocked: Boolean,
)

data class DriverQrResult(
    val name: String,
    val phoneNumber: String,
    val destination: String,
    val driverId: String,
)