package com.jawharat.manifest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddVehicleRequestBody(
    @SerialName("vehicleNumber")
    val vehicleNumber: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("carType")
    val carType: String? = null,
    @SerialName("price")
    val price: Int? = null,
    @SerialName("driverId")
    val driverId: String? = null,
    @SerialName("line")
    val line: String? = null,
)
