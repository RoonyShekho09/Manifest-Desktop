package com.jawharat.manifest.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPrintable
import org.apache.pdfbox.printing.Scaling
import java.awt.image.BufferedImage
import java.awt.print.PageFormat
import java.awt.print.Pageable
import java.awt.print.Printable
import java.awt.print.PrinterJob
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.JobName
import javax.print.attribute.standard.MediaSizeName
import javax.print.attribute.standard.PageRanges
import javax.print.attribute.standard.PrintQuality
import javax.print.attribute.standard.Sides

sealed class PrintContent {
    data class Pdf(val pdfData: ByteArray) : PrintContent() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Pdf

            return pdfData.contentEquals(other.pdfData)
        }

        override fun hashCode(): Int {
            return pdfData.contentHashCode()
        }
    }

    data class QrCodes(val qrCode1: BufferedImage, val qrCode2: BufferedImage) : PrintContent()
}

suspend fun printContent(content: PrintContent) {
    try {
        val document = when (content) {
            is PrintContent.Pdf -> {
                if (content.pdfData.isEmpty()) {
                    return
                }

                withContext(Dispatchers.IO) {
                    PDDocument.load(content.pdfData)
                }
            }

            is PrintContent.QrCodes -> {
                buildQrDocument(content.qrCode1, content.qrCode2)
            }
        }

        document.use {
            val printerJob = PrinterJob.getPrinterJob()

            val pageable = object : Pageable {
                override fun getNumberOfPages() = document.numberOfPages

                override fun getPrintable(pageIndex: Int): Printable {
                    return PDFPrintable(
                        document,
                        Scaling.SHRINK_TO_FIT,
                        false,
                        300f
                    )
                }

                override fun getPageFormat(pageIndex: Int): PageFormat {
                    return printerJob.defaultPage()
                }
            }

            printerJob.setPageable(pageable)

            val attributes = HashPrintRequestAttributeSet()
            attributes.add(MediaSizeName.ISO_A4)
            attributes.add(Sides.ONE_SIDED)
            attributes.add(PageRanges(1, Int.MAX_VALUE))
            attributes.add(JobName("Manifest_Print_Job", null))
            attributes.add(PrintQuality.HIGH)

            if (printerJob.printDialog(attributes)) {
                val pollJob = CoroutineScope(Dispatchers.IO).launch {
                    repeat(20) {
                        delay(2000)
                    }
                }

                withContext(Dispatchers.IO) {
                    printerJob.print(attributes)
                }
                pollJob.cancel()
            } else {
                println("User cancelled the print dialog.")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
