package com.jawharat.manifest.presentation.feature.home.camera

import com.github.sarxos.webcam.Webcam
import com.jawharat.manifest.presentation.feature.home.camera.utils.increaseContrast
import com.jawharat.manifest.presentation.feature.home.camera.utils.scaleImage
import com.jawharat.manifest.presentation.feature.home.scanner.utils.decodeQrCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

interface IWebCam {
    suspend fun start(onResult: (String) -> Unit, onCameraReady: () -> Unit)
    fun stop()
}

class WebCam : IWebCam {
    private val cameraMutex = Mutex()
    private var webCam: Webcam? = null

    @OptIn(ExperimentalAtomicApi::class)
    private var isRunning = AtomicBoolean(false)

    @OptIn(ExperimentalAtomicApi::class)
    override suspend fun start(onResult: (String) -> Unit, onCameraReady: () -> Unit) =
        withContext(Dispatchers.IO) {
            cameraMutex.withLock {
                isRunning.store(true)

                if (webCam == null) {
                    webCam = Webcam.getDefault() ?: return@withLock

                    if (webCam?.isOpen == false)
                        webCam?.viewSizes?.maxByOrNull { it.width * it.height }
                            ?.let { webCam?.viewSize = it }

                    webCam?.open()
                }


                delay(1000)

                withContext(Dispatchers.Main) {
                    onCameraReady()
                }

                while (isActive && isRunning.load()) {
                    val image = webCam?.image ?: run {
                        delay(100)
                        continue
                    }

                    try {
                        val frame = drawIntoReusableBuffer(image)
                        val scaled = scaleImage(frame)
                        val contrasted = increaseContrast(scaled)
                        val result = decodeQrCode(contrasted)
                        contrasted.flush()
                        frame.flush()
                        image.flush()

                        withContext(Dispatchers.Main) {
                            when (result) {
                                is QRResult.Found -> {
                                    println("QR detected")
                                    runCatching { onResult(result.value) }
                                }

                                is QRResult.NotFound -> {
                                    println("🔍 No QR detected")
                                }

                                is QRResult.Error -> {
                                    println("❌ Decode error: ${result.message}")
                                }
                            }
                        }
                        delay(1500)
                    } finally {
                        image.flush()
                    }
                }
            }
        }

    @OptIn(ExperimentalAtomicApi::class)
    override fun stop() {
        isRunning.store(false)
    }

    private var reusableGraphics: Graphics2D? = null
    private var reusableBuffer: BufferedImage? = null

    private fun drawIntoReusableBuffer(source: BufferedImage): BufferedImage {
        if (reusableBuffer == null ||
            reusableBuffer!!.width != source.width ||
            reusableBuffer!!.height != source.height
        ) {
            reusableBuffer?.flush()
            reusableBuffer = BufferedImage(source.width, source.height, BufferedImage.TYPE_INT_RGB)
            reusableGraphics?.dispose()
            reusableGraphics = reusableBuffer!!.createGraphics()
        }
        reusableGraphics!!.drawImage(source, 0, 0, null)
        source.flush()
        return reusableBuffer!!
    }
}

sealed class QRResult {
    data class Found(val value: String) : QRResult()
    data class NotFound(val message: String) : QRResult()
    data class Error(val message: String) : QRResult()
}
