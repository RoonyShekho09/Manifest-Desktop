package com.jawharat.manifest.data.remote.model.dispatches

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DispatchQrCodeResponse(
    @SerialName("line")
    val line: String? = null,
    @SerialName("price")
    val price: Int? = null,
    @SerialName("vehicle-number")
    val vehicleNumber: String? = null,
    @SerialName("vehicle-type")
    val vehicleType: String? = null
)