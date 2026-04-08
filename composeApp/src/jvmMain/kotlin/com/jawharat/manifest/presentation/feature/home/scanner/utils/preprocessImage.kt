package com.jawharat.manifest.presentation.feature.home.scanner.utils

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel

fun preprocessImage(input: BufferedImage): BufferedImage {
    val gray = BufferedImage(input.width, input.height, BufferedImage.TYPE_BYTE_GRAY)
    val g2d = gray.createGraphics()
    g2d.drawImage(input, 0, 0, null)
    g2d.dispose()

    val scaled = BufferedImage(gray.width * 2, gray.height * 2, BufferedImage.TYPE_BYTE_GRAY)
    val sg = scaled.createGraphics()
    sg.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC
    )
    sg.drawImage(gray, 0, 0, scaled.width, scaled.height, null)
    sg.dispose()

    val sharpenKernel = Kernel(
        3, 3,
        floatArrayOf(
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f
        )
    )
    val sharpen = ConvolveOp(sharpenKernel, ConvolveOp.EDGE_NO_OP, null)
    return sharpen.filter(scaled, null)
}
