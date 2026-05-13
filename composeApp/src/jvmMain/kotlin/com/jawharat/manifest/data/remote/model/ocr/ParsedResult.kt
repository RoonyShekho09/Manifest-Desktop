package com.jawharat.manifest.data.remote.model.ocr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParsedResult(
    @SerialName("ErrorDetails")
    val errorDetails: String? = null,
    @SerialName("ErrorMessage")
    val errorMessage: String? = null,
    @SerialName("FileParseExitCode")
    val fileParseExitCode: Int? = null,
    @SerialName("ParsedText")
    val parsedText: String? = null,
    @SerialName("TextOrientation")
    val textOrientation: String? = null,
    @SerialName("TextOverlay")
    val textOverlay: TextOverlay? = null
)