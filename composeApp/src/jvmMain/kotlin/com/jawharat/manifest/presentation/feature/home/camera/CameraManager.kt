package com.jawharat.manifest.presentation.feature.home.camera

import com.jawharat.manifest.utils.toUnit
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_videoio.CAP_MSMF
import org.bytedeco.opencv.opencv_java
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


interface ICameraManager {
    val frameFlow: SharedFlow<BufferedImage>
    fun start()
    fun stop()
    fun release()
}

class CameraManager : ICameraManager {

    private val _frameFlow = MutableSharedFlow<BufferedImage>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val frameFlow: SharedFlow<BufferedImage> = _frameFlow

    @OptIn(ExperimentalAtomicApi::class)
    private var isRunning = AtomicBoolean(false)
    var capture: VideoCapture? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val isOpenCvLoaded = CompletableDeferred<Unit>()

    init {
        scope.launch {
            runCatching {
                Loader.load(opencv_java::class.java)
            }.onSuccess {
                isOpenCvLoaded.complete(Unit)
            }
        }
    }

    override fun start() {
        if (capture != null) return
        scope.launch {
            isOpenCvLoaded.await()
            capture = VideoCapture(0, CAP_MSMF)
            val frame = Mat()
            try {
                while (isActive) {
                    if (capture!!.read(frame) && !frame.empty()) {
                        _frameFlow.emit(frame.toBufferedImage())
                    } else {
                        capture!!.release()
                        capture = null
                        delay(2000)
                        capture = VideoCapture(0, CAP_MSMF)
                    }
                    yield()
                }
            } finally {
                frame.release()
                capture?.release()
                capture = null
            }
        }
    }

    private var sourcePixels: ByteArray? = null
    private var reusableImage: BufferedImage? = null

    fun Mat.toBufferedImage(): BufferedImage {
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

    @OptIn(ExperimentalAtomicApi::class)
    override fun stop() {
        isRunning.store(false)
    }

    override fun release() = capture?.release().toUnit()
}

sealed class QRResult {
    data class Found(val value: String) : QRResult()
    data class NotFound(val message: String) : QRResult()
    data class Error(val message: String) : QRResult()
}
