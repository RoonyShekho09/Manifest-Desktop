package com.jawharat.manifest.utils.qr

import androidx.compose.foundation.ExperimentalFoundationApi
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage

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
