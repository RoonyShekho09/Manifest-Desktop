package com.jawharat.manifest.data.remote.model.auth


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null
)