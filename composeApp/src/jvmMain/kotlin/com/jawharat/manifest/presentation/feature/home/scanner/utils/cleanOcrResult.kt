package com.jawharat.manifest.presentation.feature.home.scanner.utils

import Pr22.Imaging.RawImage
import Pr22.Processing.Document
import Pr22.Processing.FieldReference
import PrIns.Exceptions.EntryNotFound
import PrIns.Exceptions.InvalidParameter
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

fun String.cleanOcrResult(): String {
    return this.lines().joinToString("\n") { line ->
        if (line.isMrzLine()) {
            line.replace("(", "C")
                .replace(")", "C")
                .replace(" ", "<")
                .replace("0", "O")
        } else line
    }
}

fun String.isMrzLine(): Boolean {
    return length >= 30 && all { it.isLetterOrDigit() || it == '<' || it == '(' || it == ')' || it == ' ' }
}

fun cleanImageForOcr(source: BufferedImage): BufferedImage {
    val gray = BufferedImage(source.width, source.height, BufferedImage.TYPE_BYTE_GRAY)
    val g = gray.createGraphics()
    g.drawImage(source, 0, 0, null)
    g.dispose()

    for (y in 0 until gray.height) {
        for (x in 0 until gray.width) {
            val color = gray.getRGB(x, y) and 0xFF
            if (color < 160) {
                gray.setRGB(x, y, 0x000000)
            } else {
                gray.setRGB(x, y, 0xFFFFFF)
            }
        }
    }
    return gray
}

fun printDocFields(doc: Document) {
    val fields: MutableList<FieldReference> = doc.fields

    System.out.printf("  %1$-20s%2$-17s%3\$s%n", "FieldId", "Status", "Value")
    System.out.printf("  %1$-20s%2$-17s%3\$s%n", "-------", "------", "-----")

    println()

    for (currentFieldRef in fields) {
        try {
            println("currentFieldRef: $currentFieldRef")
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
            } else {
                System.out.printf("  %1$-20s%2$-17s[%3\$s]%n", fieldName, status, value)
                System.out.printf("\t%2$-31s[%1\$s]%n", formattedValue, "   - Formatted")
                System.out.printf("\t%2$-31s[%1\$s]%n", standardizedValue, "   - Standardized")
            }

            println("current field: $currentField")

            val lst = currentField.detailedStatus
            for (chk in lst) {
                println("detailed: $chk")
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


fun parseOcrToPerson(text: String): PersonDocument {
    fun extract(label: String): String? {
        val regex = Regex("$label\\s*:?\\s*([^|]+)")
        return regex.find(text)?.groupValues?.get(1)?.trim()
    }

    val name = extract("الأسم / ناو")
    val father = extract("الأب / باوك")
    val grandfather = extract("الجد / بابير")
    val surname = extract("اللقب /")
    val maternalGrandfather = extract("الجد / بابير")
    val gender = extract("الجنس /")

    val numbers = Regex("\\d+").findAll(text).map { it.value }.toList()

    val nationalId = numbers.firstOrNull()

    return PersonDocument(
        fullName = "$name $father $grandfather $surname",
        gender = gender,
        documentId = nationalId,
        countryCode = "iq",
        dateOfBirth = "",
        documentType = "ID",
    )
}

fun parsePersonDocument(ocrText: String): PersonDocument {
    val lines = ocrText.lines().map { it.trim() }.filter { it.isNotEmpty() }

    fun extractValue(line: String): String? {
        return line.substringAfterLast(":").trim().takeIf { it.isNotEmpty() }
    }

    fun findLine(vararg keywords: String): String? {
        return lines.firstOrNull { line -> keywords.any { line.contains(it) } }
    }

    val nameLine = findLine("الاسم")
    val fatherLine = findLine("الأب", "الاب")
    val grandFatherLine = findLine("الجد")
    val familyLine = findLine("اللقب")

    val fullName = listOfNotNull(
        nameLine?.let { extractValue(it) },
        fatherLine?.let { extractValue(it) },
        grandFatherLine?.let { extractValue(it) },
        familyLine?.let { extractValue(it) }
    ).joinToString(" ").takeIf { it.isNotEmpty() }

    val gender = findLine("الجنس")?.let { extractValue(it) }

    val nationalId = lines.firstOrNull { it.matches(Regex("\\d{12}")) }

    val documentNumber = lines.firstOrNull { it.matches(Regex("[A-Z]\\d{7,12}")) }

    val fullText = lines.joinToString(" ")
    val documentType = when {
        fullText.contains("باسيورت") || fullText.contains("جواز") -> "PASSPORT"
        fullText.contains("البطاقة الوطنية") -> "NATIONAL_ID"
        else -> null
    }

    return PersonDocument(
        fullName = fullName,
        dateOfBirth = null,
        countryCode = null,
        documentId = documentNumber ?: nationalId,
        gender = gender,
        documentType = documentType
    )
}


data class PersonDocument(
    val fullName: String?,
    val dateOfBirth: String?,
    val countryCode: String?,
    val documentId: String?,
    val gender: String?,
    val documentType: String?
)


fun extractPersonDocument(doc: Document): PersonDocument {
    var fullName: String? = null
    var dateOfBirth: String? = null
    var countryCode: String? = null
    var documentId: String? = null
    var sex: String? = null
    var documentType: String? = null

    for (fieldRef in doc.fields) {
        try {
            val field = doc.getField(fieldRef)

            val value = try {
                field.formattedStringValue
            } catch (e: Exception) {
                null
            }

            when (fieldRef.toString()) {
                "MrzName" -> fullName = value
                "MrzBirthDate" -> dateOfBirth = field.standardizedStringValue
                "MrzIssueCountry", "MrzNationality" -> countryCode = value
                "MrzDocumentNumber" -> documentId = value
                "MrzSex" -> sex = value
                "MrzDocType" -> documentType = value
            }

        } catch (e: Exception) {
            println("Exception: $e")
        }
    }

    return PersonDocument(
        fullName = fullName,
        dateOfBirth = dateOfBirth,
        countryCode = countryCode,
        documentId = documentId,
        gender = sex,
        documentType = documentType
    )
}

fun preprocessImage(input: BufferedImage): BufferedImage {
    val gray = BufferedImage(input.width, input.height, BufferedImage.TYPE_BYTE_GRAY)
    val g2d = gray.createGraphics()
    g2d.drawImage(input, 0, 0, null)
    g2d.dispose()

    val scaled = BufferedImage(gray.width * 2, gray.height * 2, BufferedImage.TYPE_BYTE_GRAY)
    val sg = scaled.createGraphics()
    sg.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC
    )
    sg.drawImage(gray, 0, 0, scaled.width, scaled.height, null)
    sg.dispose()

    val sharpenKernel = Kernel(
        3, 3,
        floatArrayOf(
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f
        )
    )
    val sharpen = ConvolveOp(sharpenKernel, ConvolveOp.EDGE_NO_OP, null)
    return sharpen.filter(scaled, null)
}

fun adaptiveThreshold(
    source: BufferedImage,
    blockSize: Int = 15,
    offset: Int = 10
): BufferedImage {
    val width = source.width
    val height = source.height
    val result = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    val gray = Array(height) { y ->
        IntArray(width) { x ->
            val color = Color(source.getRGB(x, y))
            (color.red * 0.299 + color.green * 0.587 + color.blue * 0.114).toInt()
        }
    }

    val half = blockSize / 2

    for (y in 0 until height) {
        for (x in 0 until width) {
            // Compute local mean in the surrounding block
            var sum = 0
            var count = 0
            for (dy in -half..half) {
                for (dx in -half..half) {
                    val ny = (y + dy).coerceIn(0, height - 1)
                    val nx = (x + dx).coerceIn(0, width - 1)
                    sum += gray[ny][nx]
                    count++
                }
            }
            val localMean = sum / count

            val pixel =
                if (gray[y][x] < localMean - offset) Color.BLACK.rgb else Color.WHITE.rgb
            result.setRGB(x, y, pixel)
        }
    }

    return result
}

fun dilate(source: BufferedImage): BufferedImage {
    val width = source.width
    val height = source.height
    val result = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    for (y in 0 until height) {
        for (x in 0 until width) {
            // Take the darkest pixel in a 2x2 neighborhood
            var minGray = 255
            for (dy in 0..1) {
                for (dx in 0..1) {
                    val ny = (y + dy).coerceIn(0, height - 1)
                    val nx = (x + dx).coerceIn(0, width - 1)
                    val c = Color(source.getRGB(nx, ny))
                    val gray = (c.red * 0.299 + c.green * 0.587 + c.blue * 0.114).toInt()
                    if (gray < minGray) minGray = gray
                }
            }
            val grayColor = Color(minGray, minGray, minGray).rgb
            result.setRGB(x, y, grayColor)
        }
    }
    return result
}

fun preprocess(source: BufferedImage): BufferedImage {
    val scaled = BufferedImage(source.width * 2, source.height * 2, BufferedImage.TYPE_INT_RGB)
    val g = scaled.createGraphics()
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC
    )
    g.drawImage(source, 0, 0, scaled.width, scaled.height, null)
    g.dispose()

    return dilate(scaled)
}

fun BufferedImage.compressForOcr(maxWidth: Int = 1000): String {
    val scaled = if (width > maxWidth) {
        val ratio = maxWidth.toDouble() / width
        val newHeight = (height * ratio).toInt()
        val resized = BufferedImage(maxWidth, newHeight, type)
        resized.createGraphics().apply {
            setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
            )
            drawImage(this@compressForOcr, 0, 0, maxWidth, newHeight, null)
            dispose()
        }
        resized
    } else this


    val outputStream = ByteArrayOutputStream()
    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    val params = writer.defaultWriteParam.apply {
        compressionMode = ImageWriteParam.MODE_EXPLICIT
        compressionQuality = 0.8f
    }
    writer.output = ImageIO.createImageOutputStream(outputStream)
    writer.write(null, IIOImage(scaled, null, null), params)
    writer.dispose()

    val base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray())
    return "data:image/jpeg;base64,$base64"
}



fun optimizeForIraqiID(source: BufferedImage): BufferedImage {
    val width = source.width
    val height = source.height
    val result = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val color = Color(source.getRGB(x, y))
            val grayValue =
                (color.red * 0.299 + color.green * 0.587 + color.blue * 0.114).toInt()

            if (grayValue < 120) {
                result.setRGB(x, y, Color.BLACK.rgb)
            } else {
                result.setRGB(x, y, Color.WHITE.rgb)
            }
        }
    }
    return result
}
