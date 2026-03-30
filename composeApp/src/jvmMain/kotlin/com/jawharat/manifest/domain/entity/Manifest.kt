package com.jawharat.manifest.domain.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Manifest(
    val date: String = "2026-03-29",
    val price: String = "0.00",
    val from: String = "Unknown",
    val to: String = "Unknown",
    val vehicleNumber: String = "0000",
    val vehicleType: String = "Standard",
    val driverName: String = "Pending Assignment",
    val driverIdNumber: String = "000000",
    val driverPhoneNumber: String = "000-000-0000",
)

fun parseManifest(text: String): Manifest {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    return json.decodeFromString<Manifest>(text)
}