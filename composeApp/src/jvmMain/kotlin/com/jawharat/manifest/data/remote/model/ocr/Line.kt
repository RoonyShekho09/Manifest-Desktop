package com.jawharat.manifest.data.remote.model.ocr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Line(
    @SerialName("LineText")
    val lineText: String? = null,
    @SerialName("MaxHeight")
    val maxHeight: Double = 0.0,
    @SerialName("MinTop")
    val minTop: Double = 0.0,
    @SerialName("Words")
    val words: List<Word> = emptyList()
)