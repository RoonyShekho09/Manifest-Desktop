package com.jawharat.manifest.presentation.feature.home.camera.utils


import com.jawharat.manifest.presentation.feature.home.camera.QRResult
import com.jawharat.manifest.presentation.feature.home.scanner.utils.decodeQrCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.awt.Graphics2D
import java.awt.image.BufferedImage


suspend fun processImage(image: BufferedImage, onResult: (String) -> Unit) {
    var frame: BufferedImage? = null
    var scaled: BufferedImage? = null
    var contrasted: BufferedImage? = null
    try {
        frame = drawIntoReusableBuffer(image)
        scaled = scaleImage(frame)
        contrasted = increaseContrast(scaled)
        val result = decodeQrCode(contrasted)

        withContext(Dispatchers.Main) {
            when (result) {
                is QRResult.Found -> runCatching { onResult(result.value) }
                is QRResult.NotFound -> println("No QR detected")
                is QRResult.Error -> println("Decode error: ${result.message}")
            }
        }
        delay(1500)
    } finally {
        contrasted?.flush()
        scaled?.flush()
        frame?.flush()
    }
}


private var reusableGraphics: Graphics2D? = null
private var reusableBuffer: BufferedImage? = null
private fun drawIntoReusableBuffer(source: BufferedImage): BufferedImage {
    if (reusableBuffer == null ||
        reusableBuffer?.width != source.width ||
        reusableBuffer?.height != source.height
    ) {
        reusableBuffer?.flush()
        reusableBuffer = BufferedImage(source.width, source.height, BufferedImage.TYPE_INT_RGB)
        reusableGraphics?.dispose()
        reusableGraphics = reusableBuffer?.createGraphics()
    }
    reusableGraphics?.drawImage(source, 0, 0, null)
    source.flush()
    return reusableBuffer ?: BufferedImage(0, 0, 0)
}
