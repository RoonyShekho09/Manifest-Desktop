package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.sarxos.webcam.Webcam
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.RescaleOp

@Composable
fun QrCodeScanner(
    onResult: (String) -> Unit,
    onFrame: (ImageBitmap) -> Unit,
) {
    var webcam by remember { mutableStateOf<Webcam?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var frameCount by remember { mutableStateOf(0) }
    val onResultRef by rememberUpdatedState(onResult)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val cam = Webcam.getDefault() ?: run {
                    withContext(Dispatchers.Main) { println("❌ No camera found") }
                    return@withContext
                }
                cam.viewSizes.maxByOrNull { it.width * it.height }?.let { cam.viewSize = it }
                cam.open()
                delay(1000)

                withContext(Dispatchers.Main) {
                    webcam = cam
                    isLoading = false
                }

                while (true) {
                    val image = cam.image ?: run {
                        withContext(Dispatchers.Main) { println("⚠️ Got null frame") }
                        continue
                    }

                    val snapshot =
                        BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB).also {
                            it.createGraphics().apply {
                                drawImage(image, 0, 0, null)
                                dispose()
                            }
                        }

                    onFrame(snapshot.toComposeImageBitmap())

                    withContext(Dispatchers.Main) {
                        frameCount++
                        println("📷 Frame #$frameCount | Size: ${image.width}x${image.height}")
                    }

                    val scaled = scaleImage(snapshot)
                    val contrasted = increaseContrast(scaled)

                    val result = decodeQrCode(contrasted)

                    withContext(Dispatchers.Main) {
                        when (result) {
                            is QRResult.Found -> {
                                println("✅ QR Found: ${result.value}")
                                runCatching { onResultRef(result.value) }
                            }

                            is QRResult.NotFound -> {
                                println("🔍 Frame #$frameCount - No QR detected")
                            }

                            is QRResult.Error -> {
                                println("❌ Decode error: ${result.message}")
                            }
                        }
                    }
                    delay(200)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("💥 Exception: ${e.message}")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { webcam?.let { if (it.isOpen) it.close() } }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

sealed class QRResult {
    data class Found(val value: String) : QRResult()
    data class NotFound(val message: String) : QRResult()
    data class Error(val message: String) : QRResult()
}

private fun decodeQrCode(image: BufferedImage): QRResult {
    return try {
        val width = image.width
        val height = image.height

        val pixels = IntArray(width * height)
        image.getRGB(0, 0, width, height, pixels, 0, width)

        val source = RGBLuminanceSource(width, height, pixels)
        val hints = mapOf(
            DecodeHintType.TRY_HARDER to true,
            DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)
        )

        val result = tryDecode(BinaryBitmap(HybridBinarizer(source)), hints)
            ?: tryDecode(BinaryBitmap(GlobalHistogramBinarizer(source)), hints)
            ?: return QRResult.NotFound("No QR found with either binarizer")

        QRResult.Found(result.text)
    } catch (e: Exception) {
        QRResult.Error(e::class.simpleName + ": " + e.message)
    }
}

private fun tryDecode(bitmap: BinaryBitmap, hints: Map<DecodeHintType, Any>): Result? {
    return try {
        MultiFormatReader().decode(bitmap, hints)
    } catch (e: NotFoundException) {
        null
    }
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
