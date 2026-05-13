package com.jawharat.manifest.presentation.feature.home.camera

import com.jawharat.manifest.presentation.feature.home.camera.utils.BufferImageProcessor
import com.jawharat.manifest.presentation.feature.home.camera.utils.reusableBuffer
import com.jawharat.manifest.presentation.feature.home.camera.utils.reusableGraphics
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
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


interface ICameraManager {
    val frameFlow: SharedFlow<BufferedImage>
    fun start()
    fun stop()
    fun release()
    fun clean()
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
    private var captureScope: CoroutineScope? = null

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
        captureScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        captureScope?.launch {
            isOpenCvLoaded.await()
            capture = VideoCapture(0, CAP_MSMF)
            val frame = Mat()
            try {
                while (isActive) {
                    if (capture!!.read(frame) && !frame.empty()) {
                        val reused = BufferImageProcessor().convert(frame)
                        val copy = BufferedImage(reused.width, reused.height, reused.type)
                        val g = copy.createGraphics()
                        g.drawImage(reused, 0, 0, null)
                        g.dispose()
                        frame.release()
                        _frameFlow.emit(copy)
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

    @OptIn(ExperimentalAtomicApi::class)
    override fun stop() {
        isRunning.store(false)
    }

    override fun release() = capture?.release().toUnit()

    override fun clean() {
        BufferImageProcessor().release()
        reusableGraphics?.dispose()
        reusableGraphics = null
        reusableBuffer?.flush()
        reusableBuffer = null
    }
}

sealed class QRResult {
    data class Found(val value: String) : QRResult()
    data class NotFound(val message: String) : QRResult()
    data class Error(val message: String) : QRResult()
}
