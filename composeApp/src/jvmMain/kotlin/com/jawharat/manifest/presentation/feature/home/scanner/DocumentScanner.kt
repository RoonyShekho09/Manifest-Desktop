package com.jawharat.manifest.presentation.feature.home.scanner

import Pr22.DocumentReaderDevice
import Pr22.Engine
import Pr22.Events.DetectionEventArgs
import Pr22.Events.DeviceUpdate
import Pr22.Events.DocFrameFound
import Pr22.Events.PageEventArgs
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
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.feature.home.scanner.utils.PersonDocument
import com.jawharat.manifest.presentation.feature.home.scanner.utils.cleanOcrResult
import com.jawharat.manifest.presentation.feature.home.scanner.utils.compressForOcr
import com.jawharat.manifest.presentation.feature.home.scanner.utils.extractPersonDocument
import com.jawharat.manifest.presentation.feature.home.scanner.utils.preprocessImage
import com.jawharat.manifest.presentation.feature.home.scanner.utils.printDocFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage

interface IDocumentScanner {
    val isSoftwareInstalled: Boolean
    suspend fun scan(onResult: (PersonDocument) -> Unit)
    fun stop()
}

class DocumentScanner(private val repository: ManifestRepository) : IDocumentScanner {
    override var isSoftwareInstalled: Boolean = true
    val device: DocumentReaderDevice? by lazy {
        runCatching { DocumentReaderDevice() }
            .onFailure {
                isSoftwareInstalled = false
            }
            .getOrNull()
    }
    val vizReadingTask = EngineTask().apply { add(FieldSource.Viz, FieldId.All) }
    val mrzReadingTask = EngineTask().apply { add(FieldSource.Mrz, FieldId.All) }
    val engine: Engine? by lazy { device?.engine }
    var isDocumentPresent = false
    var isInitial = true
    var liveTask: TaskControl? = null
    private var initialized = false
    private suspend fun ensureInitialized() {
        if (initialized) return
        withContext(Dispatchers.IO) {
            device?.useDevice(0)
            addScanEvents()
            eventListener()
            initialized = true
        }
    }

    override suspend fun scan(onResult: (PersonDocument) -> Unit) {
        ensureInitialized()
        val scanner = device?.scanner

        liveTask = scanner?.startTask(FreerunTask.detection())

        if (!isDocumentPresent) return

        val scanTask = DocScannerTask()
        scanTask.add(Light.White).add(Light.Infra)
        val docPage = scanner?.scan(scanTask, PagePosition.First)
        var vizDocPage: Page?

        try {
            docPage?.let {
                analyzeWithMrz(docPage = docPage, onResult = onResult)
            }

            scanTask.add(Light.All)

            vizDocPage = scanner?.scan(scanTask, PagePosition.Current)
            vizDocPage?.let {
                analyzeWithViz(vizDocPage)
            }

        } finally {
            //        scanner?.cleanUpLastPage()
//            scanTask.del(Light.White).del(Light.Infra).del(Light.All)
//            docPage?.del(Light.All)
            //    scanner?.cleanUpData()
            liveTask?.Stop()
            isDocumentPresent = false
        }
    }

    override fun stop() {
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
        isInitial = true
        initialized = false
    }

    fun scanId(image: BufferedImage, onResult: (PersonDocument) -> Unit) {
        GlobalScope.launch {
            val response = repository.ocrSpace(image.compressForOcr(), engine = "2")

            response.parsedResults?.firstOrNull()?.parsedText?.let {
                println("response: " + it.cleanOcrResult())
                //   println("response: ${parseOcrToPerson(cleanOcrText(it))}")
            }
        }
    }

    private fun analyzeWithViz(docPage: Page) {
        val vizDoc = engine?.analyze(docPage, vizReadingTask)

        docPage.selectByIndex(0).toImage()?.let {
            scanId(preprocessImage(it), onResult = { println("ID information: $it") })
        }

        vizDoc?.let {
            println("viz: " + extractPersonDocument(it))
        }

        vizDoc?.toVariant()?.clear()
    }

    private fun analyzeWithMrz(docPage: Page, onResult: (PersonDocument) -> Unit) {
        val mrzDoc = engine?.analyze(docPage, mrzReadingTask)

        mrzDoc?.let {
            printDocFields(it)
        }

        mrzDoc?.let {
            onResult(extractPersonDocument(it))
            println("mrz: " + extractPersonDocument(it))
        }

        mrzDoc?.toVariant()?.clear()
    }

    fun eventListener() {
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
    fun addScanEvents() {
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

        device?.addEventListener(
            object : DocFrameFound {
                override fun onDocFrameFound(e: PageEventArgs) {
                    println("Document frame found. Page: " + e.page)
                }
            }
        )
    }
}
