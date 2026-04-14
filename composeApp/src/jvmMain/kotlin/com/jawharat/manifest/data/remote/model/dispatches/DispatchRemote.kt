package com.jawharat.manifest.data.remote.model.dispatches


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DispatchRemote(
    @SerialName("carType")
    val carType: String? = null,
    @SerialName("driverId")
    val driverId: String? = null,
    @SerialName("_id")
    val id: String? = null,
    @SerialName("isInside")
    val isInside: Boolean? = null,
    @SerialName("line")
    val line: String? = null,
    @SerialName("price")
    val price: Int? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("vehicleNumber")
    val vehicleNumber: String? = null
)