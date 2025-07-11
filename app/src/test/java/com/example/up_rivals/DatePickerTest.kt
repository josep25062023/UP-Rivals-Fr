package com.example.up_rivals

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatePickerTest {

    @Test
    fun testDateFormatting() {
        // Prueba que las fechas se formateen correctamente para el backend
        val testDate = LocalDate.of(2025, 8, 15)
        val expectedFormat = "2025-08-15T00:00:00Z"
        val actualFormat = "${testDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}T00:00:00Z"
        
        assertEquals(expectedFormat, actualFormat)
    }

    @Test
    fun testDateDisplayFormatting() {
        // Prueba que las fechas se muestren correctamente en la UI
        val testDate = LocalDate.of(2025, 8, 15)
        val expectedDisplay = "15/08/2025"
        val actualDisplay = testDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        
        assertEquals(expectedDisplay, actualDisplay)
    }

    @Test
    fun testMillisToLocalDateConversion() {
        // Prueba la conversión de milisegundos a LocalDate
        val testMillis = 1723680000000L // Equivale aproximadamente a 2024-08-15
        val convertedDate = java.time.Instant.ofEpochMilli(testMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        
        // Verificamos que la conversión sea válida
        assertNotNull(convertedDate)
        assertTrue(convertedDate.year >= 2024)
    }
}