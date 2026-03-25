package com.jawharat.manifest.data.local.model.vehicles


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriverInformationRemote(
    @SerialName("destination")
    val destination: String? = null,
    @SerialName("_id")
    val _id: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null
)