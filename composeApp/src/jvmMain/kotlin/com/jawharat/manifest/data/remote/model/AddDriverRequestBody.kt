package com.jawharat.manifest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddDriverRequestBody(
    @SerialName("driverId")
    val driverId: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,
    @SerialName("destination")
    val destination: String? = null,
)
