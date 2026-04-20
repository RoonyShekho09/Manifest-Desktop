package com.jawharat.manifest.utils


import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.image.BufferedImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class QrDocumentBuilderTest {
    private fun createDummyImage(): BufferedImage {
        return BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
    }

    @Test
    fun `buildQrDocument should create single page document with the specified page size`() {
        val qr1 = createDummyImage()
        val qr2 = createDummyImage()
        val pageSize = PDRectangle.A1

        buildQrDocument(qr1, qr2, pageSize = pageSize).use { document ->
            assertEquals(1, document.numberOfPages)
            val page = document.getPage(0)

            assertEquals(pageSize.width, page.mediaBox.width, 0.1f)
            assertEquals(pageSize.height, page.mediaBox.height, 0.1f)
        }
    }

    @Test
    fun `buildQrDocument should embed images as XObjects`() {
        val qr1 = createDummyImage()
        val qr2 = createDummyImage()

        buildQrDocument(qr1, qr2).use { document ->
            val page = document.getPage(0)
            val resources = page.resources

            val imageCount = resources.xObjectNames.count { name ->
                resources.getXObject(name) is PDImageXObject
            }

            assertTrue(imageCount >= 2, "Should have at least 2 embedded images")
        }
    }

    @Test
    fun `buildQrDocument should handle larger input images without crashing`() {
        val qr1 = BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB)
        val qr2 = BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB)

        buildQrDocument(qr1, qr2).use { document ->
            assertNotNull(document)
            assertEquals(1, document.numberOfPages)
        }
    }
}
