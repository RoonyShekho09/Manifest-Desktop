package com.jawharat.manifest.domain.entity.ocr


data class OcrLine(
    val text: String,
    val maxHeight: Double,
    val minTop: Double,
    val ocrWords: List<OcrWord> = emptyList()
)
