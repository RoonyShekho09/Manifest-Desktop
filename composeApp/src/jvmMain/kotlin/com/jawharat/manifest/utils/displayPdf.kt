package com.jawharat.manifest.utils

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.GridBagLayout
import java.awt.Image
import java.awt.Toolkit
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.WindowConstants

fun displayPdf(pdfData: ByteArray) {
    val frame = JFrame("PDF Preview")
    frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    frame.layout = BorderLayout()
    val pdfPaper = createPdfComponent(pdfData)
    frame.add(pdfPaper, BorderLayout.CENTER)
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
}

fun createPdfComponent(pdfData: ByteArray): JComponent {
    val pdDocument = PDDocument.load(pdfData)
    val image = pdDocument.use { doc ->
        val renderer = PDFRenderer(doc)
        renderer.renderImageWithDPI(0, 150f)
    }

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val insets = Toolkit.getDefaultToolkit().getScreenInsets(
        GraphicsEnvironment.getLocalGraphicsEnvironment()
            .defaultScreenDevice.defaultConfiguration
    )
    val maxWidth = screenSize.width - insets.left - insets.right - 40
    val maxHeight = screenSize.height - insets.top - insets.bottom - 80

    val scaleX = maxWidth.toDouble() / image.width
    val scaleY = maxHeight.toDouble() / image.height
    val scale = minOf(scaleX, scaleY, 0.5)

    val scaledWidth = (image.width * scale).toInt()
    val scaledHeight = (image.height * scale).toInt()

    val scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)

    val pageLabel = JLabel(ImageIcon(scaledImage)).apply {
        background = Color.WHITE
        isOpaque = true
        border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
    }

    return JPanel(GridBagLayout()).apply {
        background = Color(240, 240, 240)
        add(pageLabel)
        preferredSize = Dimension(scaledWidth + 20, scaledHeight + 20)
    }
}
