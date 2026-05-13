package com.jawharat.manifest.domain.entity.manifest

data class Driver(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val destination: String,
    val driverId: String,
    val blocked: Boolean,
)