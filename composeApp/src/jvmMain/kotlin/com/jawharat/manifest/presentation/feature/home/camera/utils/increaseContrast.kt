package com.jawharat.manifest.presentation.feature.home.camera.utils


import java.awt.image.BufferedImage
import java.awt.image.RescaleOp

 fun increaseContrast(scaled: BufferedImage): BufferedImage {
    return try {
        val op = RescaleOp(1.5f, -30f, null)
        op.filter(
            scaled.let {
                BufferedImage(it.width, it.height, BufferedImage.TYPE_INT_ARGB).also { rgb ->
                    rgb.createGraphics().apply { drawImage(it, 0, 0, null); dispose() }
                }
            },
            null
        )
    } catch (e: Exception) {
        scaled
    }
}

