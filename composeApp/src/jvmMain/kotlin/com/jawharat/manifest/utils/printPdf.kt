package com.jawharat.manifest.utils

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPrintable
import org.apache.pdfbox.printing.Scaling
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

fun printPdf(pdfData: ByteArray, onStatusChange: (String) -> Unit) {
    if (pdfData.isEmpty()) return

    try {
        PDDocument.load(pdfData).use { document ->
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
                onStatusChange("Printing started...")
                printerJob.print(attributes)
                onStatusChange("Success: Sent to spooler.")
            } else {
                onStatusChange("User cancelled the print dialog.")
            }
        }
    } catch (e: Exception) {
        onStatusChange("Error: ${e.message}")
        e.printStackTrace()
    }
}
