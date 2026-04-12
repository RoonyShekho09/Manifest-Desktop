package com.jawharat.manifest.data.remote.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceMatrix(
    @SerialName("هەولێر - بەغداد")
    val erbilBaghdad: RouteDetail? = null,
    @SerialName("هەولێر - خانەقین")
    val erbilKhanaqin: RouteDetail? = null,
    @SerialName("هەولێر - سلێمانی")
    val erbilSulaymaniyah: RouteDetail? = null,
    @SerialName("هەولێر - پردێ")
    val erbilPerde: RouteDetail? = null,
    @SerialName("هەولێر - چەمچەماڵ")
    val erbilChamchamal: RouteDetail? = null,
    @SerialName("هەولێر - ڕومادی")
    val erbilRamadi: RouteDetail? = null,
    @SerialName("هەولێر - کفری")
    val erbilKifri: RouteDetail? = null,
    @SerialName("هەولێر - کەرکووک")
    val erbilKirkuk: RouteDetail? = null,
    @SerialName("هەولێر - کەلار")
    val erbilKalar: RouteDetail? = null
)
