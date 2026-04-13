package com.jawharat.manifest.domain.entity


data class OcrLine(
    val text: String,
    val maxHeight: Double,
    val minTop: Double,
    val words: List<Word> = emptyList()
)

data class Word(
    val height: Double = 0.0,
    val left: Double = 0.0,
    val top: Double = 0.0,
    val width: Double = 0.0,
    val wordText: String? = null
)
