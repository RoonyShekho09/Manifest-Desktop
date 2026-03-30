package com.jawharat.manifest.data.remote.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineRemote(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null
)