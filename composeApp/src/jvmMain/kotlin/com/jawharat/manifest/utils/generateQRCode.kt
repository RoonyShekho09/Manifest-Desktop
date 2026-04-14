package com.jawharat.manifest.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import java.awt.BorderLayout
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.WindowConstants

fun generateQRCode(
    text: String,
    width: Int = 400,
    height: Int = 400,
    displayQrCode: Boolean = true
): BufferedImage {
    val bitMatrix = MultiFormatWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        width,
        height
    )

    val image = MatrixToImageWriter.toBufferedImage(bitMatrix)

    val frame = JFrame("QR Code Preview")
    frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    frame.layout = BorderLayout()

    val button = JButton("Copy to Clipboard").apply {
        isContentAreaFilled = false
        isFocusPainted = false
        isOpaque = true
        cursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR)

        background = java.awt.Color(52, 152, 219)
        foreground = java.awt.Color.WHITE
        font = java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)
        border = javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20)

        addMouseListener(
            object : java.awt.event.MouseAdapter() {
                override fun mouseEntered(e: java.awt.event.MouseEvent) {
                    if (isEnabled) background = java.awt.Color(41, 128, 185)
                }

                override fun mouseExited(e: java.awt.event.MouseEvent) {
                    if (isEnabled) background = java.awt.Color(52, 152, 219)
                }
            }
        )

        addActionListener {
            copyQrToClipboard(bitMatrix)

            val originalText = this.text
            val originalColor = background

            this.text = "✓ Copied to Clipboard"
            background = java.awt.Color(46, 204, 113)
            isEnabled = false

            javax.swing.Timer(1500) {
                this.text = originalText
                background = originalColor
                isEnabled = true
            }.apply {
                isRepeats = false
                start()
            }
        }
    }

    frame.add(JLabel(ImageIcon(image)), BorderLayout.CENTER)
    frame.add(button, BorderLayout.SOUTH)
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isVisible = displayQrCode

    return image
}

@OptIn(ExperimentalFoundationApi::class)
fun copyQrToClipboard(bitMatrix: BitMatrix) {
    val image = MatrixToImageWriter.toBufferedImage(bitMatrix)
    val selection = ImageTransferable(image)
    Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)
}

class ImageTransferable(private val image: BufferedImage) : Transferable {
    override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
    override fun isDataFlavorSupported(flavor: DataFlavor) = flavor == DataFlavor.imageFlavor
    override fun getTransferData(flavor: DataFlavor): Any {
        if (isDataFlavorSupported(flavor)) return image
        throw UnsupportedOperationException()
    }
}
