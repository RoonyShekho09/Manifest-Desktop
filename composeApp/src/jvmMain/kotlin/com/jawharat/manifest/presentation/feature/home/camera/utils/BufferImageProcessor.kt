package com.jawharat.manifest.presentation.feature.home.camera.utils

import org.opencv.core.Mat
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

class BufferImageProcessor {
    private var sourcePixels: ByteArray? = null
    private var reusableImage: BufferedImage? = null

    fun convert(mat: Mat): BufferedImage = with(mat) {
        val width = cols()
        val height = rows()
        val channels = channels()
        val size = width * height * channels

        if (sourcePixels == null || sourcePixels!!.size != size) {
            sourcePixels = ByteArray(size)
            val type =
                if (channels > 1) BufferedImage.TYPE_3BYTE_BGR else BufferedImage.TYPE_BYTE_GRAY
            reusableImage = BufferedImage(width, height, type)
        }

        get(0, 0, sourcePixels!!)
        val targetPixels = (reusableImage!!.raster.dataBuffer as DataBufferByte).data
        System.arraycopy(sourcePixels!!, 0, targetPixels, 0, size)
        return reusableImage!!
    }

    fun release() {
        reusableImage?.flush()
        sourcePixels = null
        reusableImage = null
    }
}
