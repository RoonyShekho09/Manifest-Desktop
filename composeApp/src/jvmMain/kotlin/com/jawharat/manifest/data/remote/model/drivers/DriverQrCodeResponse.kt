package com.jawharat.manifest.data.remote.model.drivers


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriverQrCodeResponse(
    @SerialName("driver-id")
    val driverId: String? = null,
    @SerialName("driver-name")
    val driverName: String? = null,
    @SerialName("phone-number")
    val phoneNumber: String? = null,
    @SerialName("to")
    val to: String? = null,
    @SerialName("blocked")
    val blocked: Boolean = false,
)
