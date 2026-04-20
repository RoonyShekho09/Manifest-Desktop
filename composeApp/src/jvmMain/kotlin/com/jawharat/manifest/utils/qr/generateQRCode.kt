package com.jawharat.manifest.utils.qr

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.Timer
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
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

        background = Color(52, 152, 219)
        foreground = Color.WHITE
        font = Font("SansSerif", Font.BOLD, 14)
        border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        addMouseListener(
            object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    if (isEnabled) background = Color(41, 128, 185)
                }

                override fun mouseExited(e: MouseEvent) {
                    if (isEnabled) background = Color(52, 152, 219)
                }
            }
        )

        addActionListener {
            copyQrToClipboard(bitMatrix)

            val originalText = this.text
            val originalColor = background

            this.text = "✓ Copied to Clipboard"
            background = Color(46, 204, 113)
            isEnabled = false

            Timer(1500) {
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
