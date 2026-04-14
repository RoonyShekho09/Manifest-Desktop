package com.jawharat.manifest.data.remote.model.dispatches


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddDispatchResponse(
    @SerialName("message")
    val message: String? = null,
    @SerialName("vehicle")
    val dispatch: DispatchRemote? = null
)