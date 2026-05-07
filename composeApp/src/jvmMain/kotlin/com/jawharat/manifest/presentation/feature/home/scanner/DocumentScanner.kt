package com.jawharat.manifest.presentation.feature.home.scanner

import Pr22.DocumentReaderDevice
import Pr22.DocumentReaderDevice.getDeviceList
import Pr22.Engine
import Pr22.Events.DetectionEventArgs
import Pr22.Events.DeviceUpdate
import Pr22.Events.PresenceStateChanged
import Pr22.Events.UpdateEventArgs
import Pr22.Imaging.Light
import Pr22.Imaging.PagePosition
import Pr22.Processing.FieldId
import Pr22.Processing.FieldSource
import Pr22.Processing.Page
import Pr22.Task.DocScannerTask
import Pr22.Task.EngineTask
import Pr22.Task.FreerunTask
import Pr22.Task.TaskControl
import Pr22.Util.PresenceState
import PrIns.Exceptions.General
import com.jawharat.manifest.presentation.feature.home.scanner.utils.extractFromPassport
import com.jawharat.manifest.domain.entity.PersonDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.awt.image.BufferedImage

interface IDocumentScanner {
    val isSoftwareInstalled: Boolean
    suspend fun scan(
        onResult: (PersonDocument) -> Unit,
        onScan: (BufferedImage) -> Unit
    )
}

class DocumentScanner(private val deviceProvider: () -> DocumentReaderDevice? = { DocumentReaderDevice() }) :
    IDocumentScanner {
    override var isSoftwareInstalled: Boolean = true
    private var device: DocumentReaderDevice? = null
    private val mrzReadingTask = EngineTask().apply { add(FieldSource.Mrz, FieldId.All) }
    private val engine: Engine? by lazy { device?.engine }

    @Volatile
    private var isDocumentPresent = false
    private var liveTask: TaskControl? = null
    private var initialized = false
    private val initMutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val stopMutex = Mutex()

    init {
        Runtime.getRuntime().addShutdownHook(
            Thread {
                runBlocking {
                    withTimeout(5_000) {
                        stop()
                    }
                }
            }
        )

        runCatching { deviceProvider() }
            .onSuccess {
                device = it
                isSoftwareInstalled = true
            }
            .onFailure { isSoftwareInstalled = false }
    }

    private suspend fun ensureInitialized(reconnect: Boolean = false) {
        initMutex.withLock {
            if (reconnect || !initialized) {
                withContext(Dispatchers.IO) {
                    runCatching {
                        waitForDevice()
                        device?.useDevice(0)
                        addScanEvents()
                        eventListener()
                        initialized = true
                    }.onFailure { e ->
                        println("Initialization failed: ${e.message}")
                    }
                }
            } else {
                return@withLock
            }
        }
    }

    private suspend fun waitForDevice(timeout: Long = 120_000) =
        withTimeout(timeout) {
            while (true) {
                if (getDeviceList().isNotEmpty()) break
                delay(1000)
            }
        }

    override suspend fun scan(
        onResult: (PersonDocument) -> Unit,
        onScan: (BufferedImage) -> Unit
    ) {
        ensureInitialized()

        val scanner = device?.scanner

        runCatching {
            liveTask = scanner?.startTask(FreerunTask.detection())
        }.onFailure {
            if (it is PrIns.Exceptions.NoSuchDevice)
                ensureInitialized(reconnect = true)
        }

        if (!isDocumentPresent)
            return

        val scanTask = DocScannerTask()
        scanTask.add(Light.White).add(Light.Infra)
        val docPage = scanner?.scan(scanTask, PagePosition.First)

        docPage?.selectByIndex(0)?.toImage()?.let { onScan(it) }

        try {
            docPage?.let {
                analyzeWithMrz(
                    docPage = docPage,
                    onResult = onResult,
                    onResultNotFound = { }
                )
            }
        } finally {
            scanner?.cleanUpLastPage()
            scanTask.del(Light.White)
            docPage?.del(Light.All)
            scanner?.cleanUpData()
            liveTask?.Stop()
            isDocumentPresent = false
        }
    }

    private fun stop() {
        scope.launch {
            stopMutex.withLock {
                runCatching {
                    device?.scanner?.let { scanner ->
                        scanner.cleanUpLastPage()
                        scanner.cleanUpData()
                    }
                    device?.close()
                }.onFailure {
                    println("Stop failed: $it")
                }
                isDocumentPresent = false
                liveTask?.Stop()
                initialized = false
            }
        }
        scope.cancel()
    }

    private fun analyzeWithMrz(
        docPage: Page,
        onResult: (PersonDocument) -> Unit,
        onResultNotFound: (BufferedImage) -> Unit
    ) {
        val mrzDoc = engine?.analyze(docPage, mrzReadingTask)

        if (mrzDoc?.fields?.isEmpty() == true) {
            docPage.selectByIndex(0).toImage()?.let {
                onResultNotFound(it)
            }
        }

        mrzDoc?.let {
            onResult(extractFromPassport(it))
        }

        mrzDoc?.toVariant()?.clear()
    }

    private fun eventListener() {
        device?.addEventListener(
            object : DeviceUpdate {
                override fun onDeviceUpdate(e: UpdateEventArgs) {
                    when (e.part) {
                        1 -> println("  Reading calibration file from device.")
                        2 -> println("  Scanner firmware update.")
                        4 -> println("  RFID reader firmware update.")
                        5 -> println("  License update.")
                    }
                }
            }
        )
    }

    @Throws(General::class)
    private fun addScanEvents() {
        var called = false
        device?.addEventListener(
            object : PresenceStateChanged {
                override fun onStateChanged(e: DetectionEventArgs) {
                    if (e.state == PresenceState.NoMove) {
                        if (!called) {
                            println("Not moving")
                            isDocumentPresent = true
                            called = true
                        }
                        called = true
                    } else {
                        called = false
                    }
                }
            }
        )
    }
}
