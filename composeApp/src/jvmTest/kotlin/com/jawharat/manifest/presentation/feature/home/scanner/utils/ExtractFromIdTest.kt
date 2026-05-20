package com.jawharat.manifest.presentation.feature.home.scanner.utils

import com.jawharat.manifest.domain.entity.ocr.OcrLine
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNull

class IdExtractionTests {
    @Test
    fun testDocumentIdExtractionAlphaNumeric() {
        val lines = listOf(
            OcrLine("A1234567", 20.0, 264.0),
            OcrLine("الاسم : زيد", 20.0, 218.0),
            OcrLine("الأب : طارق", 20.0, 186.0),
            OcrLine("الجد : محمود", 20.0, 149.0)
        )
        val result = extractFromId(lines)
        assertEquals("A1234567", result?.documentId)
    }

    @Test
    fun testMultipleUnassociatedTextChunks() {
        val lines = listOf(
            OcrLine("123456789012", 20.0, 264.0),
            OcrLine("جمهورية العراق", 20.0, 292.0),
            OcrLine("علي", 20.0, 250.0),
            OcrLine("مصطفى كمال محمود السامرائي", 20.0, 218.0),
            OcrLine("وزارة الداخلية", 20.0, 200.0)
        )
        val result = extractFromId(lines)
        assertEquals("مصطفى كمال محمود السامرائي", result?.fullName)
    }

    @Test
    fun testEmptyList() {
        val result = extractFromId(emptyList())
        assertEquals("", result?.fullName)
        assertEquals("", result?.documentId)
    }

    @Test
    fun testCustomMinimumLength() {
        val lines = listOf(
            OcrLine("IQ000001", 20.0, 264.0),
            OcrLine("الاسم : علي", 20.0, 218.0),
            OcrLine("الأب : حسن", 20.0, 186.0)
        )
        val result = extractFromId(lines, fullNameMinimumLength = 2)
        assertEquals("علي حسن", result?.fullName)
    }

    @Test
    fun testDocumentIdExtractionNumeric() {
        val lines = listOf(
            OcrLine("123456789012", 20.0, 264.0),
            OcrLine("الاسم : زينب", 20.0, 218.0),
            OcrLine("الأب : علي", 20.0, 186.0),
            OcrLine("الجد : حسن", 20.0, 149.0)
        )
        val result = extractFromId(lines)
        assertEquals("123456789012", result?.documentId)
    }

    @Test
    fun testCleanLabeledCard() {
        val lines = listOf(
            OcrLine("البطاقة الوطنية", 20.0, 292.0),
            OcrLine("IQ123456", 20.0, 264.0),
            OcrLine("الاسم : علي", 20.0, 218.0),
            OcrLine("الأب : حسن", 20.0, 186.0),
            OcrLine("الجد : محمد", 20.0, 149.0),
            OcrLine("اللقب : العبيدي", 20.0, 124.0)
        )
        val result = extractFromId(lines)
        assertEquals("علي حسن محمد العبيدي", result?.fullName)
    }

    @Test
    fun testBilingualWithColon() {
        val lines = listOf(
            OcrLine("البطاقة الوطنية/ كارني", 33.0, 292.0),
            OcrLine("199502913065", 39.0, 266.0),
            OcrLine("الاسم / ناو : مند", 30.0, 232.0),
            OcrLine("الأب / باوك : سعيد", 39.0, 196.0),
            OcrLine("الجد / بابير : حاج علي", 50.0, 158.0),
            OcrLine("اللقب / نازناو : لاوند", 36.0, 136.0)
        )
        val result = extractFromId(lines)
        assertEquals("مند سعيد حاج علي لاوند", result?.fullName)
    }

    @Test
    fun testBilingualWithSplitLines() {
        val lines = listOf(
            OcrLine("199502913065", 39.0, 266.0),
            OcrLine("الاسم / ناو", 30.0, 232.0),
            OcrLine(": مند", 26.0, 230.0),
            OcrLine("الأب / باوك : سعيد", 39.0, 196.0),
            OcrLine("الجد / بابير : حاج علي", 50.0, 158.0),
            OcrLine("اللقب / نازناو : لاوند", 36.0, 136.0)
        )
        val result = extractFromId(lines)
        assertEquals("مند سعيد حاج علي لاوند", result?.fullName)
    }

    @Test
    fun testNoColon() {
        val lines = listOf(
            OcrLine("C98765432", 20.0, 264.0),
            OcrLine("الاسم سارة", 20.0, 218.0),
            OcrLine("الأب أحمد", 20.0, 186.0),
            OcrLine("الجد يوسف", 20.0, 149.0),
            OcrLine("اللقب الدليمي", 20.0, 124.0)
        )
        val result = extractFromId(lines)
        assertEquals("سارة أحمد يوسف الدليمي", result?.fullName)
    }

    @Test
    fun testFullNameField() {
        val lines = listOf(
            OcrLine("IQ987654", 20.0, 264.0),
            OcrLine("الاسم الكامل : فاطمة زهراء كريم النجار", 20.0, 218.0)
        )
        val result = extractFromId(lines)
        assertEquals("فاطمة زهراء كريم النجار", result?.fullName)
    }

    @Test
    fun testUnlabeled() {
        val lines = listOf(
            OcrLine("B456789", 20.0, 264.0),
            OcrLine("جمهورية العراق", 20.0, 292.0),
            OcrLine("زينب عمر كريم السامرائي", 20.0, 218.0)
        )
        val result = extractFromId(lines)
        assertEquals("زينب عمر كريم السامرائي", result?.fullName)
    }

    @Test
    fun testDuplicateGrandfather() {
        val lines = listOf(
            OcrLine("199502913065", 39.0, 266.0),
            OcrLine("الاسم / ناو : مند", 30.0, 232.0),
            OcrLine("الأب / باوك : سعيد", 39.0, 196.0),
            OcrLine("الجد / بابير : حاج علي", 50.0, 158.0),
            OcrLine("اللقب / نازناو : لاوند", 36.0, 136.0),
            OcrLine("الام / دايك : شكريه", 28.0, 106.0),
            OcrLine("الجد / بابير : حسن", 26.0, 63.0)
        )
        val result = extractFromId(lines)
        assertEquals("مند سعيد حسن لاوند", result?.fullName)
    }

    @Test
    fun testNoisyOcr() {
        val lines = listOf(
            OcrLine("IQ000111", 20.0, 264.0),
            OcrLine("الاسم : _علي#", 20.0, 218.0),
            OcrLine("الأب : حسن  ", 20.0, 186.0),
            OcrLine("الجد : محمد2", 20.0, 149.0),
            OcrLine("اللقب : الجبوري", 20.0, 124.0)
        )
        val result = extractFromId(lines)
        assertEquals("علي حسن محمد الجبوري", result?.fullName)
    }

    @Test
    fun testPassportEarlyReturn() {
        val lines = listOf(
            OcrLine("Passport", 20.0, 292.0),
            OcrLine("الاسم الكامل : محمد علي حسن", 20.0, 218.0),
            OcrLine("IQ111222", 20.0, 264.0)
        )
        assertNull(extractFromId(lines))
    }

    @Test
    fun testNullInput() {
        assertNull(extractFromId(null))
    }

    @Test
    fun testShortName() {
        val lines = listOf(
            OcrLine("C000001", 20.0, 264.0),
            OcrLine("الاسم : علي", 20.0, 218.0),
            OcrLine("الأب : حسن", 20.0, 186.0)
        )
        val result = extractFromId(lines)
        assertEquals("", result?.fullName)
    }

    @Test
    fun testSyrianCard() {
        val lines = listOf(
            OcrLine("Syrian Arab Republic", 20.0, 292.0),
            OcrLine("الاسم : ريم", 20.0, 218.0),
            OcrLine("الأب : خالد", 20.0, 186.0),
            OcrLine("الجد : إبراهيم", 20.0, 149.0)
        )
        val result = extractFromId(lines)
        assertEquals("ريم خالد إبراهيم", result?.fullName)
    }

    @Test
    fun testMisspelledLabel() {
        val lines = listOf(
            OcrLine("IQ334455", 20.0, 264.0),
            OcrLine("الاسم : نور", 20.0, 218.0),
            OcrLine("الأب : عمر", 20.0, 186.0),
            OcrLine("الجد : يوسف", 20.0, 149.0),
            OcrLine("التقب : الراوي", 20.0, 124.0)
        )
        val result = extractFromId(lines)
        assertEquals("نور عمر يوسف الراوي", result?.fullName)
    }
}
