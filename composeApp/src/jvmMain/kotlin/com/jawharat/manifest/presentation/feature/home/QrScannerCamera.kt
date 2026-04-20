package com.jawharat.manifest.presentation.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.github.sarxos.webcam.Webcam
import com.jawharat.manifest.presentation.feature.home.scanner.utils.decodeQrCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.RescaleOp
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@Composable
fun QrScannerCamera(
    onResult: (String) -> Unit,
    onCameraReady: () -> Unit,
) {
    var webcam by remember { mutableStateOf<Webcam?>(null) }
    val onResultRef by rememberUpdatedState(onResult)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val cam = Webcam.getDefault() ?: run {
                withContext(Dispatchers.Main) { println("❌ No camera found") }
                return@withContext
            }

            try {
                if (!cam.isOpen)
                    cam.viewSizes.maxByOrNull { it.width * it.height }?.let { cam.viewSize = it }

                cam.open()
                delay(1000)

                withContext(Dispatchers.Main) {
                    webcam = cam
                    onCameraReady()
                }

                while (isActive) {
                    val image = cam.image ?: continue

                    val scaled = scaleImage(image)
                    val contrasted = increaseContrast(scaled)

                    val result = decodeQrCode(contrasted)

                    withContext(Dispatchers.Main) {
                        when (result) {
                            is QRResult.Found -> {
                                runCatching { onResultRef(result.value) }
                            }

                            is QRResult.NotFound -> {
                                println("🔍 No QR detected")
                            }

                            is QRResult.Error -> {
                                println("❌ Decode error: ${result.message}")
                            }
                        }
                    }
                    delay(1200)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("💥 Exception: ${e.message}")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            println("close webcam")
            webcam?.let { if (it.isOpen) it.close() }
        }
    }
}

sealed class QRResult {
    data class Found(val value: String) : QRResult()
    data class NotFound(val message: String) : QRResult()
    data class Error(val message: String) : QRResult()
}

private fun increaseContrast(scaled: BufferedImage): BufferedImage {
    return try {
        val op = RescaleOp(1.5f, -30f, null)
        op.filter(
            scaled.let {
                BufferedImage(it.width, it.height, BufferedImage.TYPE_INT_RGB).also { rgb ->
                    rgb.createGraphics().apply { drawImage(it, 0, 0, null); dispose() }
                }
            },
            null
        )
    } catch (e: Exception) {
        scaled
    }
}

private fun scaleImage(image: BufferedImage): BufferedImage {
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
