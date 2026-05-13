package com.jawharat.manifest.presentation.feature.home.camera.utils


import com.jawharat.manifest.presentation.feature.home.camera.QRResult
import com.jawharat.manifest.presentation.feature.home.scanner.utils.decodeQrCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.awt.Graphics2D
import java.awt.image.BufferedImage


suspend fun processImage(image: BufferedImage, onResult: (String) -> Unit) {
    if (!currentCoroutineContext().isActive) {
        image.flush()
        return
    }

    val frame = drawIntoReusableBuffer(image)
    val result = decodeQrCode(frame)
    yield()
    withContext(Dispatchers.Main) {
        when (result) {
            is QRResult.Found -> runCatching { onResult(result.value) }
            is QRResult.NotFound -> println("No QR detected")
            is QRResult.Error -> println("Decode error: ${result.message}")
        }
    }
    delay(1500)
}


var reusableGraphics: Graphics2D? = null
var reusableBuffer: BufferedImage? = null
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
