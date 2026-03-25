package com.jawharat.manifest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitManifestRequestBody(
    @SerialName("driver-name")
    val driverName: String? = null,
    @SerialName("vehicle-number")
    val vehicleNumber: String? = null,
    @SerialName("vehicle-type")
    val vehicleType: String? = null,
    @SerialName("phone-number")
    val phoneNumber: String? = null,
    @SerialName("to")
    val to: String? = null,
    @SerialName("price")
    val price: String? = null,
    @SerialName("passengers")
    val passengers: List<Passenger>? = null,
    @SerialName("driver-id")
    val driverId: String? = null,
)

@Serializable
data class Passenger(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("nationality")
    val nationality: String? = null,
    @SerialName("manual")
    val manual: String? = null
)
