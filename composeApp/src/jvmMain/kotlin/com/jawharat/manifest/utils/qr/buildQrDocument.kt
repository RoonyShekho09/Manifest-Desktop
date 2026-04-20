package com.jawharat.manifest.utils.qr

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.awt.image.BufferedImage

/**
 * Builds a new single-page A4 PDDocument with the 2 QR codes placed on top of each other.
 */
fun buildQrDocument(
    qrCode1: BufferedImage,
    qrCode2: BufferedImage,
    pageSize: PDRectangle = PDRectangle.A4,
    qrSize: Float = 350f,
    spacing: Float = 20f
): PDDocument {
    val document = PDDocument()
    val page = PDPage(pageSize)
    document.addPage(page)

    val pageWidth = page.mediaBox.width
    val pageHeight = page.mediaBox.height

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
