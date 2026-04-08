package com.jawharat.manifest.presentation.feature.home.scanner.utils

import Pr22.Imaging.RawImage
import Pr22.Processing.Document
import Pr22.Processing.FieldReference
import PrIns.Exceptions.EntryNotFound
import PrIns.Exceptions.InvalidParameter

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