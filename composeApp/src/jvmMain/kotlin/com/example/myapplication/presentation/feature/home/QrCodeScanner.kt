package com.example.myapplication.presentation.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.entity.Manifest
import com.example.myapplication.domain.entity.parseManifest
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
import java.awt.image.BufferedImage

@Composable
fun QrCodeScanner(onResult: (Manifest) -> Unit, onFrame: (ImageBitmap) -> Unit) {
    var webcam by remember { mutableStateOf<Webcam?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var debugText by remember { mutableStateOf("Starting...") }
    var frameCount by remember { mutableStateOf(0) }
    val onResultRef by rememberUpdatedState(onResult)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val cam = Webcam.getDefault() ?: run {
                    withContext(Dispatchers.Main) { debugText = "❌ No camera found" }
                    return@withContext
                }
                webcam?.viewSizes?.forEach { println("Supported: ${it.width}x${it.height}") }
                cam.open()
                delay(1000)

                withContext(Dispatchers.Main) {
                    webcam = cam
                    isLoading = false
                }

                while (true) {
                    val image = cam.image ?: run {
                        withContext(Dispatchers.Main) { debugText = "⚠️ Got null frame" }
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
                        debugText = "📷 Frame #$frameCount | Size: ${image.width}x${image.height}"
                    }

                    val result = decodeQRDebug(snapshot)

                    withContext(Dispatchers.Main) {
                        when (result) {
                            is QRResult.Found -> {
                                debugText = "✅ QR Found: ${result.value}"
                                runCatching { onResultRef(parseManifest(result.value)) }
                            }

                            is QRResult.NotFound -> {
                                debugText = "🔍 Frame #$frameCount - No QR detected"
                            }

                            is QRResult.Error -> {
                                debugText = "❌ Decode error: ${result.message}"
                            }
                        }
                    }
                    delay(200)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    debugText = "💥 Exception: ${e.message}"
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { webcam?.let { if (it.isOpen) it.close() } }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        println(debugText)

        Text(
            text = debugText,
            color = Color.Red,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(8.dp)
        )
    }
}

sealed class QRResult {
    data class Found(val value: String) : QRResult()
    data class NotFound(val message: String) : QRResult()
    data class Error(val message: String) : QRResult()
}

fun decodeQRDebug(image: BufferedImage): QRResult {
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
