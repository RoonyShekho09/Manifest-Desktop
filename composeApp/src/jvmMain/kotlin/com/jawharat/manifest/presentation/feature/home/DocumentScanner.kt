package com.jawharat.manifest.presentation.feature.home

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
import Pr22.Imaging.RawImage
import Pr22.Processing.Document
import Pr22.Processing.FieldId
import Pr22.Processing.FieldReference
import Pr22.Processing.FieldSource
import Pr22.Processing.Page
import Pr22.Task.DocScannerTask
import Pr22.Task.EngineTask
import Pr22.Task.FreerunTask
import Pr22.Task.TaskControl
import Pr22.Util.PresenceState
import PrIns.Exceptions.EntryNotFound
import PrIns.Exceptions.General
import PrIns.Exceptions.InvalidParameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sourceforge.tess4j.Tesseract
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

interface IDocumentScanner {
    val isSoftwareInstalled: Boolean
    suspend fun scan(onResult: (PersonDocument) -> Unit)
    fun stop()
}

class DocumentScanner(private val tesseract: Tesseract = Tesseract()) : IDocumentScanner {
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
    private var bufferedImage: BufferedImage? = null;

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
        try {
            tesseract.setLanguage("ara")
            tesseract.setPageSegMode(6)
            tesseract.setOcrEngineMode(1)
            tesseract.setTessVariable("tessedit_char_whitelist", "ءآأؤإئابةتثجحخدذرزسشصضطظعغفقكلمنهوىيپچژگڕڵۆێ ")
            tesseract.setTessVariable("user_defined_dpi", "300")
            println("Creating tesseract...")
            println("Tesseract created OK")

            println("Image read OK: ${image.width}, ${image.height}")

            ImageIO.write(bufferedImage, "png", File("debug_scan2.png"))
            val result = tesseract.doOCR(bufferedImage)
            println(result)
        } catch (e: Error) {
            e.printStackTrace()
        }
    }

    fun cropImage(source: BufferedImage, x: Int, y: Int, w: Int, h: Int): BufferedImage {
        val subImage = source.getSubimage(x, y, w, h)
        val copy = BufferedImage(w, h, source.type)
        val g = copy.createGraphics()
        g.drawImage(subImage, 0, 0, null)
        g.dispose()
        return copy
    }

    private fun analyzeWithViz(docPage: Page) {
        val vizDoc = engine?.analyze(docPage, vizReadingTask)

        vizDoc?.let {
            printDocFields(it)
            println("viz: " + extractPersonDocument(it))
        }

        if (bufferedImage != null)
            scanId(bufferedImage!!, onResult = { })

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

        if (bufferedImage != null)
            scanId(bufferedImage!!, onResult = { })

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
                    println("state: ${e.state}")
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

    fun optimizeForIraqiID(source: BufferedImage): BufferedImage {
        val width = source.width
        val height = source.height
        val result = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val color = Color(source.getRGB(x, y))
                // IDs often have red/blue patterns.
                // We focus on the "Luminance" to find the black ink.
                val grayValue = (color.red * 0.299 + color.green * 0.587 + color.blue * 0.114).toInt()

                // THRESHOLDING:
                // If it's dark (text), make it PURE black.
                // If it's anything else (background), make it PURE white.
                if (grayValue < 120) {
                    result.setRGB(x, y, Color.BLACK.rgb)
                } else {
                    result.setRGB(x, y, Color.WHITE.rgb)
                }
            }
        }
        return result
    }

    fun cleanImageForOcr(source: BufferedImage): BufferedImage {
        val gray = BufferedImage(source.width, source.height, BufferedImage.TYPE_BYTE_GRAY)
        val g = gray.createGraphics()
        g.drawImage(source, 0, 0, null)
        g.dispose()

        for (y in 0 until gray.height) {
            for (x in 0 until gray.width) {
                val color = gray.getRGB(x, y) and 0xFF
                if (color < 160) {
                    gray.setRGB(x, y, 0x000000)
                } else {
                    gray.setRGB(x, y, 0xFFFFFF)
                }
            }
        }
        return gray
    }

    fun cleanOcrResult(raw: String): String {
        val pattern = Regex("[\\u0600-\\u06FF\\s]+")
        return pattern.findAll(raw)
            .map { it.value }
            .joinToString("")
            .replace("\\s+".toRegex(), " ") // Fix double spaces
            .trim()
    }

    private fun extractPersonDocument(doc: Document): PersonDocument {
        var fullName: String? = null
        var dateOfBirth: String? = null
        var countryCode: String? = null
        var documentId: String? = null
        var sex: String? = null
        var documentType: String? = null

        for (fieldRef in doc.fields) {
            try {
                val field = doc.getField(fieldRef)

                val value = try {
                    field.formattedStringValue
                } catch (e: Exception) {
                    null
                }

                when (fieldRef.toString()) {
                    "MrzName" -> fullName = value
                    "MrzBirthDate" -> dateOfBirth = field.standardizedStringValue
                    "MrzIssueCountry", "MrzNationality" -> countryCode = value
                    "MrzDocumentNumber" -> documentId = value
                    "MrzSex" -> sex = value
                    "MrzDocType" -> documentType = value
                }

            } catch (e: Exception) {
                println("Exception: $e")
            }
        }

        return PersonDocument(
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            countryCode = countryCode,
            documentId = documentId,
            sex = sex,
            documentType = documentType
        )
    }

    private fun printDocFields(doc: Document) {
        val fields: MutableList<FieldReference> = doc.fields

        System.out.printf("  %1$-20s%2$-17s%3\$s%n", "FieldId", "Status", "Value")
        System.out.printf("  %1$-20s%2$-17s%3\$s%n", "-------", "------", "-----")

        println()

        println("status: ${doc.status}")

        for (currentFieldRef in fields) {
            try {
                println("currentFieldRef: $currentFieldRef")
                val currentField = doc.getField(currentFieldRef)

                bufferedImage = currentField.image.toImage()

                var value: String? = ""
                var formattedValue: String? = ""
                var standardizedValue: String? = ""
                var binValue: ByteArray? = null
                try {
                    value = currentField.rawStringValue
                } catch (e: EntryNotFound) {
                } catch (e: InvalidParameter) {
                    binValue = currentField.binaryValue
                }
                try {
                    formattedValue = currentField.formattedStringValue
                } catch (e: EntryNotFound) {
                }
                try {
                    standardizedValue = currentField.standardizedStringValue
                } catch (e: EntryNotFound) {
                }
                val status = currentField.status
                val fieldName = currentFieldRef.toString()
                if (binValue != null) {
                    System.out.printf("  %1$-20s%2$-17sBinary%n", fieldName, status)
                    var cnt = 0
                    while (cnt < binValue.size) {
                        println(printBinary(binValue, cnt))
                        cnt += 16
                    }
                } else {
                    System.out.printf("  %1$-20s%2$-17s[%3\$s]%n", fieldName, status, value)
                    System.out.printf("\t%2$-31s[%1\$s]%n", formattedValue, "   - Formatted")
                    System.out.printf("\t%2$-31s[%1\$s]%n", standardizedValue, "   - Standardized")
                }

                println("current field: $currentField")

                val lst = currentField.detailedStatus
                for (chk in lst) {
                    println("detailed: $chk")
                }

                try {
                    currentField.image.save(RawImage.FileFormat.Png).save("$fieldName.png")
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
            }
        }
        println()

        for (comp in doc.fieldCompareList) {
            println(
                ("Comparing " + comp.field1 + " vs. "
                        + comp.field2 + " results " + comp.confidence)
            )
        }
        println()
    }

    private fun printBinary(arr: ByteArray, pos: Int, sz: Int = 16): String {
        var str = ""
        var str2 = ""
        var p0: Int = pos
        while (p0 < arr.size && p0 < pos + sz) {
            str += String.format("%02X", arr[p0]) + " "
            str2 += if (arr[p0] !in 0x21..0x7e) '.' else Char(arr[p0].toUShort())
            p0++
        }
        while (p0 < pos + sz) {
            str += "   "
            str2 += " "
            p0++
        }
        return str + str2
    }
}


data class PersonDocument(
    val fullName: String?,
    val dateOfBirth: String?,
    val countryCode: String?,
    val documentId: String?,
    val sex: String?,
    val documentType: String?
)
