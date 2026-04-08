package com.jawharat.manifest.presentation.feature.home.scanner.utils

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

fun BufferedImage.compressForOcr(maxWidth: Int = 1000): String {
    val scaled = if (width > maxWidth) {
        val ratio = maxWidth.toDouble() / width
        val newHeight = (height * ratio).toInt()
        val resized = BufferedImage(maxWidth, newHeight, type)
        resized.createGraphics().apply {
            setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
            )
            drawImage(this@compressForOcr, 0, 0, maxWidth, newHeight, null)
            dispose()
        }
        resized
    } else this


    val outputStream = ByteArrayOutputStream()
    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    val params = writer.defaultWriteParam.apply {
        compressionMode = ImageWriteParam.MODE_EXPLICIT
        compressionQuality = 0.8f
    }
    writer.output = ImageIO.createImageOutputStream(outputStream)
    writer.write(null, IIOImage(scaled, null, null), params)
    writer.dispose()

    val base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray())
    return "data:image/jpeg;base64,$base64"
}
