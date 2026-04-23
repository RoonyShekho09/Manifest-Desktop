package com.jawharat.manifest.presentation.feature.home.camera.utils


import java.awt.RenderingHints
import java.awt.image.BufferedImage

 fun scaleImage(image: BufferedImage): BufferedImage {
    val scale = if (image.width < 1280) 2.0 else 1.0
    val scaled = if (scale > 1.0) {
        BufferedImage(
            (image.width * scale).toInt(),
            (image.height * scale).toInt(),
            BufferedImage.TYPE_INT_ARGB
        ).also {
            it.createGraphics().apply {
                setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
                )
                drawImage(image, 0, 0, it.width, it.height, null)
                dispose()
            }
        }
    } else image
    return scaled
}
