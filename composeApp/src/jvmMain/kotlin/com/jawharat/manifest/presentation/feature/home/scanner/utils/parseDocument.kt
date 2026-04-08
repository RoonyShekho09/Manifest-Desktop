package com.jawharat.manifest.presentation.feature.home.scanner.utils

import Pr22.Processing.Document

fun parsePersonDocument(ocrText: String): PersonDocument {
    val lines = ocrText.lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    fun extractValue(line: String): String? {
        val colonIndex = line.lastIndexOf(':').takeIf { it >= 0 }
            ?: line.lastIndexOf('：').takeIf { it >= 0 }
            ?: return null
        return line.substring(colonIndex + 1).trim().takeIf { it.isNotEmpty() }
    }

    fun findValue(vararg keywords: String): String? {
        return lines
            .firstOrNull { line -> keywords.any { kw -> line.contains(kw) } }
            ?.let { extractValue(it) }
    }

    val name = findValue("الاسم")
    val father = findValue("الأب", "الاب")

    val grandfather = lines
        .firstOrNull { it.contains("الجد") && !it.contains("دايك") && !it.contains("الأم") }
        ?.let { extractValue(it) }
    val family = findValue("اللقب")

    val fullName = listOfNotNull(name, father, grandfather, family)
        .joinToString(" ")
        .takeIf { it.isNotEmpty() }

    val gender = findValue("الجنس")

    val nationalId = lines.firstOrNull { it.matches(Regex("""\d{12}""")) }

    val fullText = lines.joinToString(" ")
    val documentType = when {
        fullText.contains("باسيورت") || fullText.contains("جواز") -> "PASSPORT"
        fullText.contains("البطاقة الوطنية") || fullText.contains("كارسي") -> "NATIONAL_ID"
        else -> null
    }


    return PersonDocument(
        fullName = fullName,
        dateOfBirth = null,
        countryCode = "IQ",
        documentId = nationalId,
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
