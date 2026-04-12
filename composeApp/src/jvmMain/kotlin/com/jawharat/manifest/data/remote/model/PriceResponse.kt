package com.jawharat.manifest.data.remote.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceResponse(
    @SerialName("carTypes")
    val carTypes: List<String?>? = null,
    @SerialName("priceMatrix")
    val priceMatrix: PriceMatrix? = null
)