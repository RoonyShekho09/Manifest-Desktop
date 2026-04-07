package com.jawharat.manifest.presentation.feature.home.scanner.utils

import Pr22.Processing.Document

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
