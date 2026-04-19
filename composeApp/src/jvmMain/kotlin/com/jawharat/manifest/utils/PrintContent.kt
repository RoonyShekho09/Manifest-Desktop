package com.jawharat.manifest.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import org.apache.pdfbox.printing.PDFPrintable
import org.apache.pdfbox.printing.Scaling
import java.awt.image.BufferedImage
import java.awt.print.PageFormat
import java.awt.print.Pageable
import java.awt.print.Printable
import java.awt.print.PrinterJob
import javax.print.PrintService
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.JobName
import javax.print.attribute.standard.MediaSizeName
import javax.print.attribute.standard.PageRanges
import javax.print.attribute.standard.PrintQuality
import javax.print.attribute.standard.PrinterState
import javax.print.attribute.standard.PrinterStateReasons
import javax.print.attribute.standard.Severity
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

                val pollJob = Thread {
                    repeat(20) {
                        Thread.sleep(2000)
                        printerJob.printService?.let {
                            pollPrinterStatus(
                                it,
                                onStatusChange = { println(it) })
                        }
                    }
                }.also { it.isDaemon = true; it.start() }

                withContext(Dispatchers.IO) {
                    printerJob.print(attributes)
                }
                pollJob.interrupt()
            } else {
                println("User cancelled the print dialog.")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Builds a new single-page A4 PDDocument with the 2 QR codes placed side by side.
 */
private fun buildQrDocument(qrCode1: BufferedImage, qrCode2: BufferedImage): PDDocument {
    val document = PDDocument()
    val page = PDPage(PDRectangle.A4)
    document.addPage(page)

    val pageWidth = page.mediaBox.width
    val pageHeight = page.mediaBox.height

    val qrSize = 350f
    val spacing = 20f
    val totalHeight = (qrSize * 2) + spacing

    val startY = (pageHeight + totalHeight) / 2
    val x = (pageWidth - qrSize) / 2

    PDPageContentStream(document, page).use { contentStream ->
        val pdImage1 = LosslessFactory.createFromImage(document, qrCode1)
        contentStream.drawImage(pdImage1, x, startY - qrSize, qrSize, qrSize)

        val pdImage2 = LosslessFactory.createFromImage(document, qrCode2)
        contentStream.drawImage(pdImage2, x, startY - qrSize - spacing - qrSize, qrSize, qrSize)
    }

    return document
}

fun pollPrinterStatus(printService: PrintService, onStatusChange: (String) -> Unit) {
    val attrs = printService.attributes

    val state = attrs.get(PrinterState::class.java) as? PrinterState
    val reasons = attrs.get(PrinterStateReasons::class.java) as? PrinterStateReasons

    when (state) {
        PrinterState.IDLE -> onStatusChange("Printer is idle.")
        PrinterState.PROCESSING -> onStatusChange("Printer is processing.")
        PrinterState.STOPPED -> {
            val detail = reasons?.entries
                ?.filter { it.value == Severity.REPORT || it.value == Severity.WARNING || it.value == Severity.ERROR }
                ?.joinToString { "${it.key} (${it.value})" }
                ?: "Unknown reason"
            onStatusChange("Printer stopped: $detail")
        }

        else -> onStatusChange("Printer state unknown.")
    }
}
