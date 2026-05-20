package com.jawharat.manifest.data.remote.model.ocr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OcrResponse(
    @SerialName("name")
    val fullname: String? = null,
    @SerialName("nationality")
    val nationality: String? = null,
    @SerialName("id")
    val documentId: String? = null,
)
