package com.jawharat.manifest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PassengerRemote(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("nationality")
    val nationality: String? = null,
    @SerialName("manual")
    val manual: Boolean? = null
)
