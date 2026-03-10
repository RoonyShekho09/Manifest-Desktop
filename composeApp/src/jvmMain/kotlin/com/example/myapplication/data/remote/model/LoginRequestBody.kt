package com.example.myapplication.data.remote.model

import kotlinx.serialization.SerialName

data class LoginRequestBody(
    @SerialName("username")
    val username: String? = null,
    @SerialName("password")
    val password: String? = null,
)
