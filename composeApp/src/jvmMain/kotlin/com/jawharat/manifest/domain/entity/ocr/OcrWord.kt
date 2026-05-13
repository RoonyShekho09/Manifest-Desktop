package com.jawharat.manifest.domain.entity.ocr

data class OcrWord(
    val height: Double = 0.0,
    val left: Double = 0.0,
    val top: Double = 0.0,
    val width: Double = 0.0,
    val wordText: String? = null
)
