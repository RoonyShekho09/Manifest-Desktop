package com.jawharat.manifest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteDetail(
    @SerialName("تەکسی")
    val taxi: Int? = null,
    @SerialName("پاس")
    val bus: Int? = null,
    @SerialName("ئۆباما")
    val obama: Int? = null,
    @SerialName("جمسی خارجی")
    val gmcExternal: Int? = null,
    @SerialName("جمسی داخلی")
    val gmcInternal: Int? = null,
)