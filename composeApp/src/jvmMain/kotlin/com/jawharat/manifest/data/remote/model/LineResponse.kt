package com.jawharat.manifest.data.remote.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineResponse(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null
)