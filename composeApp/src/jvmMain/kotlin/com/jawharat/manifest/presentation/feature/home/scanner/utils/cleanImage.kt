package com.jawharat.manifest.presentation.feature.home.scanner.utils

import java.awt.image.BufferedImage

fun cleanImageForOcr(source: BufferedImage): BufferedImage {
    val gray = BufferedImage(source.width, source.height, BufferedImage.TYPE_BYTE_GRAY)
    val g = gray.createGraphics()
    g.drawImage(source, 0, 0, null)
    g.dispose()

    for (y in 0 until gray.height) {
        for (x in 0 until gray.width) {
            val color = gray.getRGB(x, y) and 0xFF
            if (color < 160) {
                gray.setRGB(x, y, 0x000000)
            } else {
                gray.setRGB(x, y, 0xFFFFFF)
            }
        }
    }
    return gray
}

fun String.fixMrzErrors(): String {
    return this.lines().map { line ->
        if (line.isMrzLine()) {
            line.replace("(", "C")
                .replace(")", "C")
                .replace(" ", "<")  // spaces are often misread as
                .replace("0", "O")  // common swap — check your data
        } else line
    }.joinToString("\n")
}

fun String.isMrzLine(): Boolean {
    return length >= 30 && all { it.isLetterOrDigit() || it == '<' || it == '(' || it == ')' || it == ' ' }
}
