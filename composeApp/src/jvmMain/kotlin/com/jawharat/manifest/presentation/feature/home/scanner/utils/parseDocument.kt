package com.jawharat.manifest.presentation.feature.home.scanner.utils

import Pr22.Processing.Document
import com.jawharat.manifest.domain.entity.OcrLine
import com.jawharat.manifest.utils.containsAny
import kotlin.math.abs

fun extractFromId(lines: List<OcrLine>?, fullNameMinimumLength: Int = 3): PersonDocument? {
    if (lines == null)
        return null

    if (lines.any { it.text.contains("Passport", ignoreCase = true) }) {
        return null
    }

    var fullName: StringBuilder? = null
    var documentId: String? = null
    var firstName = ""
    var fatherName = ""
    var grandfatherName = ""
    var surname = ""
    var lastSeenLabel = ""

    val unassociatedText = mutableListOf<String>()

    val idRegex = Regex("""[A-Z]+\d+""")
    val numericIdRegex = Regex("""\b\d{12}\b""")

    val boilerplate = listOf(
        "جمهورية", "عراق", "كوردستان", "الجنسية", "أربيل", "بلدة",
        "الاسم", "اللقب", "الولادة", "الجنس", "الاضبارة", "اثبات", "أثبات",
        "الشخصية", "كومارى", "نشينكهى", "ناوشار", "بطاقة", "وطنية",
        "فصيلة", "الدم", "Male", "Female", "Republic", "Syrian", "Arab",
        "وزارة", "الداخلية", "سه لماندنى", "كمسايه", "كه سايه", "ادايت", "وضبة"
    )

    for (line in lines) {
        val text = line.text.trim()

        if (documentId == null) {
            val alphaMatch = idRegex.find(text)
            if (alphaMatch != null) {
                documentId = alphaMatch.value
            } else {
                val numMatch = numericIdRegex.find(text)
                if (numMatch != null) {
                    documentId = numMatch.value
                }
            }
        }

        if (text.contains(":")) {
            val parts = text.split(":", limit = 2)
            var label = parts[0].trim()
            val value = parts[1].trim()

            if (label.isEmpty() && lastSeenLabel.isNotEmpty()) {
                label = lastSeenLabel
            }

            if (value.isNotBlank()) {
                when {
                    label.contains("الاسم الكامل") || label.contains(
                        "Full NAME",
                        true
                    ) -> {
                        println("value: $value")
                        fullName = StringBuilder(value)
                    }

                    label.containsAny("الاسم", "الأسم") && !label.contains("الام") -> firstName =
                        value

                    label.containsAny("الاب", "الأب") -> fatherName = value
                    label.contains("الجد") -> grandfatherName = value
                    label.contains("اللقب") || label.contains("التقب") -> surname = value
                }
            }
            lastSeenLabel = ""
        } else {
            val keys = listOf(
                "الاسم",
                "الأسم",
                "الاب",
                "الأب",
                "الجد",
                "اللقب",
                "التقب",
                "ناو",
                "Full NAME"
            )

            if (keys.any { text.contains(it, ignoreCase = true) }) {
                var cleanText = text
                for (key in keys) {
                    cleanText = cleanText.replace(key, "")
                }
                cleanText = cleanText.replace(Regex("""[^\p{L}\s]"""), "").trim()

                if (cleanText.isNotBlank()) {
                    if (fullName == null)
                        fullName = StringBuilder(cleanText)
                    else
                        fullName.append(" $cleanText")
                }

                lastSeenLabel = text
            }

            val isBoilerplate = boilerplate.any { text.contains(it, ignoreCase = true) }
            val isOnlyArabicLetters = text.matches(Regex("""^[\p{IsArabic}\s]+$"""))

            if (!isBoilerplate && isOnlyArabicLetters) {
                unassociatedText.add(text)
            }
        }
    }

    if (fullName == null) {
        val combinedName = listOf(firstName, fatherName, grandfatherName, surname)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        if (combinedName.isNotBlank()) {
            fullName = StringBuilder(combinedName)
        }
    }

    if (fullName == null && unassociatedText.isNotEmpty()) {
        val possibleNames = unassociatedText.filter {
            val wordCount = it.split("\\s+".toRegex()).size
            wordCount in 2..4
        }

        if (possibleNames.isNotEmpty()) {
            fullName = StringBuilder(possibleNames.maxByOrNull { it.split("\\s+".toRegex()).size })
        }
    }

    if ((fullName?.split("\\s+".toRegex())?.size ?: 0) < fullNameMinimumLength) {
        fullName = null
    }

    return PersonDocument(
        fullName = cleanName(fullName?.toString()?.ifEmpty { lastSeenLabel }.orEmpty()).orEmpty(),
        countryCode = "IQ",
        documentId = documentId.orEmpty(),
        gender = "",
        documentType = ""
    )
}

private fun cleanName(raw: String): String? {
    val cleaned = raw
        .replace(Regex("""[_\-*#!@$%^&()=+\[\]{}<>|\\/"']"""), " ")
        .replace(Regex("""\d+"""), " ")
        .replace(Regex("""[a-zA-Z]"""), " ")
        .replace(Regex("""\s{2,}"""), " ")
        .trim()

    val hasArabic = cleaned.any { it in '\u0600'..'\u06FF' }
    return if (cleaned.length >= 2 && hasArabic) cleaned else null
}

data class PersonDocument(
    val fullName: String,
    val countryCode: String,
    val documentId: String,
    val gender: String,
    val documentType: String
)

fun extractFromPassport(doc: Document): PersonDocument {
    var fullName: String? = null
    var countryCode: String? = null
    var documentId: String? = null
    var sex: String? = null
    var documentType: String? = null

    for (fieldRef in doc.fields) {
        try {
            val field = doc.getField(fieldRef)

            val value = try {
                field.formattedStringValue
            } catch (_: Exception) {
                null
            }

            when (fieldRef.toString()) {
                "MrzName" -> fullName = value
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
        fullName = fullName.orEmpty(),
        countryCode = countryCode.orEmpty(),
        documentId = documentId.orEmpty(),
        gender = sex.orEmpty(),
        documentType = documentType.orEmpty()
    )
}
