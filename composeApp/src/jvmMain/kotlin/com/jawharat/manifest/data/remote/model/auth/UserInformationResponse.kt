package com.jawharat.manifest.data.remote.model.auth


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInformationResponse(
    @SerialName("location")
    val location: Location? = null,
    @SerialName("name")
    val name: String? = null
)
