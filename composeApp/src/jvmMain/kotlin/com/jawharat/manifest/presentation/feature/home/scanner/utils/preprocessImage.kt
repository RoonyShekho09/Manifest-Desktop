package com.jawharat.manifest.presentation.feature.home.scanner.utils

import java.awt.Color
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

fun adaptiveThreshold(
    source: BufferedImage,
    blockSize: Int = 15,
    offset: Int = 10
): BufferedImage {
    val width = source.width
    val height = source.height
    val result = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    val gray = Array(height) { y ->
        IntArray(width) { x ->
            val color = Color(source.getRGB(x, y))
            (color.red * 0.299 + color.green * 0.587 + color.blue * 0.114).toInt()
        }
    }

    val half = blockSize / 2

    for (y in 0 until height) {
        for (x in 0 until width) {
            // Compute local mean in the surrounding block
            var sum = 0
            var count = 0
            for (dy in -half..half) {
                for (dx in -half..half) {
                    val ny = (y + dy).coerceIn(0, height - 1)
                    val nx = (x + dx).coerceIn(0, width - 1)
                    sum += gray[ny][nx]
                    count++
                }
            }
            val localMean = sum / count

            val pixel =
                if (gray[y][x] < localMean - offset) Color.BLACK.rgb else Color.WHITE.rgb
            result.setRGB(x, y, pixel)
        }
    }

    return result
}

fun dilate(source: BufferedImage): BufferedImage {
    val width = source.width
    val height = source.height
    val result = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    for (y in 0 until height) {
        for (x in 0 until width) {
            var minGray = 255
            for (dy in 0..1) {
                for (dx in 0..1) {
                    val ny = (y + dy).coerceIn(0, height - 1)
                    val nx = (x + dx).coerceIn(0, width - 1)
                    val c = Color(source.getRGB(nx, ny))
                    val gray = (c.red * 0.299 + c.green * 0.587 + c.blue * 0.114).toInt()
                    if (gray < minGray) minGray = gray
                }
            }
            val grayColor = Color(minGray, minGray, minGray).rgb
            result.setRGB(x, y, grayColor)
        }
    }
    return result
}

fun preprocess(source: BufferedImage): BufferedImage {
    val scaled = BufferedImage(source.width * 2, source.height * 2, BufferedImage.TYPE_INT_RGB)
    val g = scaled.createGraphics()
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC
    )
    g.drawImage(source, 0, 0, scaled.width, scaled.height, null)
    g.dispose()

    return dilate(scaled)
}


fun optimizeForIraqiID(source: BufferedImage): BufferedImage {
    val width = source.width
    val height = source.height
    val result = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val color = Color(source.getRGB(x, y))
            val grayValue =
                (color.red * 0.299 + color.green * 0.587 + color.blue * 0.114).toInt()

            if (grayValue < 120) {
                result.setRGB(x, y, Color.BLACK.rgb)
            } else {
                result.setRGB(x, y, Color.WHITE.rgb)
            }
        }
    }
    return result
}
