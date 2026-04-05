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
import Pr22.Util.PresenceState
import PrIns.Exceptions.EntryNotFound
import PrIns.Exceptions.General
import PrIns.Exceptions.InvalidParameter
import PrIns.Exceptions.NoSuchDevice

interface IDocumentScanner {
    val isSoftwareInstalled: Boolean
    fun scan(onResult: (PersonDocument) -> Unit)
    fun stop()
}

class DocumentScanner : IDocumentScanner {

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
    val scanTask = DocScannerTask()

    val engine: Engine? by lazy { device?.engine }
    var isDocumentPresent = false
    var isInitial = true

    init {
        runCatching {
            println("initialize!!!!")
            device?.useDevice(0)
            addScanEvents()
            eventListener()
        }.onFailure {
            if (it is NoSuchDevice) {
                println("No device found!")
            }
        }
    }

    override fun scan(onResult: (PersonDocument) -> Unit) {
        if (!isInitial)
            device?.useDevice(0)

        isInitial = false

        val scanner = device?.scanner

        val liveTask = scanner?.startTask(FreerunTask.detection())

        if (!isDocumentPresent) return

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
            scanner?.cleanUpLastPage()
            scanTask.del(Light.White).del(Light.Infra).del(Light.All)
            docPage?.del(Light.All)
            scanner?.cleanUpData()
            println("stop task")
            liveTask?.Stop()
            isDocumentPresent = false
            device?.close()
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
        isInitial = true
    }

    private fun analyzeWithViz(docPage: Page) {
        var vizDoc = engine?.analyze(docPage, vizReadingTask)

        runCatching {
            vizDoc?.save(Document.FileFormat.Xml)?.save("VIZ.xml")
        }.onFailure {
            println("Saving MRZ.xml failed: $it")
        }

        vizDoc?.toVariant()?.clear()
        vizDoc = null
    }

    private fun analyzeWithMrz(docPage: Page, onResult: (PersonDocument) -> Unit) {
        var mrzDoc = engine?.analyze(docPage, mrzReadingTask)

        runCatching {
            mrzDoc?.save(Document.FileFormat.Xml)?.save("MRZ.xml")
        }.onFailure {
            println("Saving MRZ.xml failed: $it")
        }

        mrzDoc?.let {
            onResult(extractPersonDocument(it))
        }

        mrzDoc?.toVariant()?.clear()
        mrzDoc = null
    }

    fun eventListener() {
        device?.addEventListener(
            object : DeviceUpdate {
                override fun onDeviceUpdate(e: UpdateEventArgs) {
                    println("Update event.")
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
                        println("Not moving")
                        isDocumentPresent = true
                        if (!called) {
                            println("Not moving")
                            isDocumentPresent = true
                        }
                        called = true
                    } else {
                        called = false
                    }
                }
            }
        )

//        device?.addEventListener(
//            object : ImageScanned {
//                override fun onImageScanned(e: ImageEventArgs) {
//                    println("Image scanned. Page: " + e.page + " Light: " + e.light)
//                }
//            }
//        )

//        device?.addEventListener(
//            object : ScanFinished {
//                override fun onScanFinished(e: PageEventArgs) {
//                    println("Page scanned. Page: " + e.page + " Status: " + e.getStatus())
//                }
//            }
//        )

        device?.addEventListener(
            object : DocFrameFound {
                override fun onDocFrameFound(e: PageEventArgs) {
                    println("Document frame found. Page: " + e.page)
                }
            }
        )
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

                val lst = currentField.detailedStatus
                for (chk in lst) {
                    println("detailed: $chk")
                }

                try {
                    println("detailed: " + currentField.binaryValue)
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
