package com.jawharat.manifest.data.remote.model.ocr


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OcrResponse(
    @SerialName("IsErroredOnProcessing")
    val isErroredOnProcessing: Boolean? = null,
    @SerialName("OCRExitCode")
    val oCRExitCode: Int? = null,
    @SerialName("ParsedResults")
    val parsedResults: List<ParsedResult?>? = null,
    @SerialName("ProcessingTimeInMilliseconds")
    val processingTimeInMilliseconds: String? = null
)