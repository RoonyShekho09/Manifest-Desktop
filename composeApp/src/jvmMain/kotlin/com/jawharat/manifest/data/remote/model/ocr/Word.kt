package com.jawharat.manifest.data.remote.model.ocr


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Word(
    @SerialName("Height")
    val height: Double = 0.0,
    @SerialName("Left")
    val left: Double = 0.0,
    @SerialName("Top")
    val top: Double = 0.0,
    @SerialName("Width")
    val width: Double = 0.0,
    @SerialName("WordText")
    val wordText: String? = null
)