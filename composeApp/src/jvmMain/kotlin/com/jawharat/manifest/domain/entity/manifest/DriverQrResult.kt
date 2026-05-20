package com.jawharat.manifest.domain.entity.manifest

data class DriverQrResult(
    val name: String,
    val phoneNumber: String,
    val destination: String,
    val driverId: String,
    val blocked: Boolean,
)
