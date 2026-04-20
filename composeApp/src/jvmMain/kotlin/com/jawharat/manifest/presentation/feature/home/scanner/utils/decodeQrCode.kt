package com.jawharat.manifest.presentation.feature.home.scanner.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.jawharat.manifest.presentation.feature.home.QRResult
import java.awt.image.BufferedImage


fun decodeQrCode(image: BufferedImage): QRResult {
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

        val bitmapHybrid = BinaryBitmap(HybridBinarizer(source))
        val bitmapGlobal = BinaryBitmap(GlobalHistogramBinarizer(source))

        val result = tryDecode(bitmapHybrid, hints)
            ?: tryDecode(bitmapGlobal, hints)
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
