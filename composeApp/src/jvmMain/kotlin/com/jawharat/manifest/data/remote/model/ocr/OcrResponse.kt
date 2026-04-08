package com.jawharat.manifest.data.remote.model.ocr


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OcrResponse(
    @SerialName("IsErroredOnProcessing")
    val isErroredOnProcessing: Boolean? = false,
    @SerialName("OCRExitCode")
    val oCRExitCode: Int? = 0,
    @SerialName("ParsedResults")
    val parsedResults: List<ParsedResult>? = listOf(),
    @SerialName("ProcessingTimeInMilliseconds")
    val processingTimeInMilliseconds: String? = "",
    @SerialName("SearchablePDFURL")
    val searchablePDFURL: String? = ""
)