package com.example.myapplication.data.local.model.drivers


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriverResponse(
    @SerialName("destination")
    val destination: String? = null,
    @SerialName("_id")
    val _id: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("office")
    val office: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null
)