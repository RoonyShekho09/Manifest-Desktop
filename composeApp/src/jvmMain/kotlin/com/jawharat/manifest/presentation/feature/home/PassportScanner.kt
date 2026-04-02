package com.jawharat.manifest.presentation.feature.home

import Pr22.DocumentReaderDevice
import Pr22.Events.DeviceUpdate
import Pr22.Events.DocFrameFound
import Pr22.Events.ImageEventArgs
import Pr22.Events.ImageScanned
import Pr22.Events.PageEventArgs
import Pr22.Events.ScanFinished
import Pr22.Events.ScanStarted
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
import PrIns.Exceptions.EntryNotFound
import PrIns.Exceptions.General
import PrIns.Exceptions.InvalidParameter
import PrIns.Exceptions.NoSuchDevice

interface IPassportScanner {
    fun scan()
}

class PassportScanner : IPassportScanner {

    var device: DocumentReaderDevice? = null

    init {
        runCatching {
            device?.useDevice(0)
        }.onFailure {
            if (it is NoSuchDevice) {
                println("No device found!")
            }
        }
    }

    override fun scan() {
        val scanner = device?.scanner
        val scanTask = DocScannerTask()

        println("Scanner: ${scanner}, name: ${device?.deviceName}")
        addScanEvents()
        eventListener()

        scanner?.startScanning(DocScannerTask(), PagePosition.Current)

        scanTask.add(Light.White).add(Light.Infra)
        val docPage = scanner?.scan(scanTask, PagePosition.First)

        docPage?.let {
            analyzeWithMrz(docPage)
        }

        scanTask.add(Light.All)

        val vizDocPage = scanner?.scan(scanTask, PagePosition.Current)

        vizDocPage?.let {
            analyzeWithViz(vizDocPage)
        }
    }

    private fun analyzeWithViz(docPage: Page) {
        val vizReadingTask = EngineTask()
        val ocrEngine = device?.getEngine()

        vizReadingTask.add(FieldSource.Viz, FieldId.All)
        val vizDoc = ocrEngine?.analyze(docPage, vizReadingTask)

        runCatching {
            vizDoc?.save(Document.FileFormat.Xml)?.save("VIZ.xml")
        }.onFailure {
            println("Saving MRZ.xml failed: $it")
        }
        vizDoc?.let { printDocFields(it) }
    }

    private fun analyzeWithMrz(docPage: Page) {
        val mrzReadingTask = EngineTask()
        val ocrEngine = device?.getEngine()

        mrzReadingTask.add(FieldSource.Mrz, FieldId.All)
        val mrzDoc = ocrEngine?.analyze(docPage, mrzReadingTask)

        runCatching {
            mrzDoc?.save(Document.FileFormat.Xml)?.save("MRZ.xml")
        }.onFailure {
            println("Saving MRZ.xml failed: $it")
        }

        mrzDoc?.let { printDocFields(it) }
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
        device?.addEventListener(
            object : ScanStarted {
                override fun onScanStart(e: PageEventArgs) {
                    println("Scan started. Page: " + e.page)
                }
            }
        )

        device?.addEventListener(
            object : ImageScanned {
                override fun onImageScanned(e: ImageEventArgs) {
                    println("Image scanned. Page: " + e.page + " Light: " + e.light)
                }
            }
        )

        device?.addEventListener(
            object : ScanFinished {
                override fun onScanFinished(e: PageEventArgs) {
                    println("Page scanned. Page: " + e.page + " Status: " + e.getStatus())
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

    private fun printDocFields(doc: Document) {
        val fields: MutableList<FieldReference> = doc.fields

        System.out.printf("  %1$-20s%2$-17s%3\$s%n", "FieldId", "Status", "Value")
        System.out.printf("  %1$-20s%2$-17s%3\$s%n", "-------", "------", "-----")
        println()

        for (currentFieldRef in fields) {
            try {
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
                    println(chk)
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
