package com.jawharat.manifest.data.remote.model.vehicles

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OfficeRemote(
    @SerialName("_id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null
)