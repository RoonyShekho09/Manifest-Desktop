package com.example.myapplication.data.local.model.vehicles


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleResponse(
    @SerialName("carType")
    val carType: String? = null,
    @SerialName("driverId")
    val driverId: DriverInformationRemote? = null,
    @SerialName("_id")
    val id: String? = null,
    @SerialName("isInside")
    val isInside: Boolean? = null,
    @SerialName("line")
    val line: LineRemote? = null,
    @SerialName("office")
    val office: OfficeRemote? = null,
    @SerialName("price")
    val price: Int? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("vehicleNumber")
    val vehicleNumber: String? = null
)