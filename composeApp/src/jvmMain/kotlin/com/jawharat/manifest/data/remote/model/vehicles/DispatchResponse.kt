package com.jawharat.manifest.data.remote.model.vehicles

import com.jawharat.manifest.data.remote.model.drivers.DriverInformationRemote
import com.jawharat.manifest.data.remote.model.LineRemote
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DispatchResponse(
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