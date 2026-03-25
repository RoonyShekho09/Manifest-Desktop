package com.jawharat.manifest.data.local.model.vehicles

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLocal(
    @SerialName("email")
    val email: String? = null,
    @SerialName("password")
    val password: String? = null,
)
