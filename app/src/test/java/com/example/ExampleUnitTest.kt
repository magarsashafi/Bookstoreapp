package com.example

import com.example.data.InitialBookData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun sampleBooks_arePopulatedCorrectly() {
        val books = InitialBookData.getSampleBooks()
        assertTrue("Sample books list should not be empty", books.isNotEmpty())
        assertTrue("Should have multiple Afaan Oromoo books", books.size >= 5)

        // Check Hawwii book
        val hawwii = books.find { it.title == "Hawwii" }
        assertNotNull("Hawwii book should exist", hawwii)
        assertEquals("Asoosama", hawwii?.category)
        assertEquals(0.0, hawwii?.priceEtb ?: -1.0, 0.001)
        assertTrue("Hawwii content should contain chapters", hawwii?.content?.contains("Boqonnaa") == true)

        // Check Gadaa Melbaa book
        val gadaa = books.find { it.title.contains("Gadaa") }
        assertNotNull("Gadaa Melbaa book should exist", gadaa)
        assertTrue("Gadaa Melbaa should be premium", (gadaa?.priceEtb ?: 0.0) > 0)
    }

    @Test
    fun categories_areValid() {
        val books = InitialBookData.getSampleBooks()
        val categories = books.map { it.category }.distinct()
        assertTrue(categories.contains("Asoosama"))
        assertTrue(categories.contains("Seenaa"))
        assertTrue(categories.contains("Walaloo"))
    }
}
