package com.jawharat.manifest.data.remote.model.ocr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextOverlay(
    @SerialName("HasOverlay")
    val hasOverlay: Boolean? = null,
    @SerialName("Lines")
    val lines: List<Line>? = null
)