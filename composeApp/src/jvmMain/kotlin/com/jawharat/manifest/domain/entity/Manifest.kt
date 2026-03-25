package com.jawharat.manifest.domain.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Manifest(
    val passengers: List<String> = emptyList(),
    val date: String = "",
    val price: String = "",
    val from: String = "",
    val to: String = "",
    val vehicleNumber: String = "",
    val vehicleType: String = "",
    val driverName: String = "",
    val driverIdNumber: String = "",
    val driverPhoneNumber: String = "",
)

fun parseManifest(text: String): Manifest {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    return json.decodeFromString<Manifest>(text)
}