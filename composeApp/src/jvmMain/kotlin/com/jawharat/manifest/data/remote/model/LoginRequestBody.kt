package com.jawharat.manifest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestBody(
    @SerialName("email")
    val username: String? = null,
    @SerialName("password")
    val password: String? = null,
)
