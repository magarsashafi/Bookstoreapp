package com.example

import com.example.data.InitialBookData
import com.example.data.ShelfStatus
import org.junit.Assert.assertEquals
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
        assertEquals(ShelfStatus.CURRENTLY_READING.name, hawwii?.shelfStatus)

        // Check Gadaa Melbaa book
        val gadaa = books.find { it.title.contains("Gadaa") }
        assertNotNull("Gadaa Melbaa book should exist", gadaa)
        assertTrue("Gadaa Melbaa should be premium", (gadaa?.priceEtb ?: 0.0) > 0)
        assertEquals(ShelfStatus.WANT_TO_READ.name, gadaa?.shelfStatus)
    }

    @Test
    fun sampleReviews_arePopulatedCorrectly() {
        val reviews = InitialBookData.getInitialReviews()
        assertTrue("Reviews list should not be empty", reviews.isNotEmpty())
        assertTrue("All ratings should be between 1 and 5", reviews.all { it.rating in 1..5 })
        assertTrue("Should have reviews with text", reviews.all { it.reviewText.isNotBlank() })
    }

    @Test
    fun defaultProfile_isValid() {
        val profile = InitialBookData.getDefaultProfile()
        assertNotNull(profile)
        assertEquals("megersa_shafi", profile.username)
        assertTrue(profile.email.contains("@"))
        assertTrue(profile.isLoggedIn)
    }

    @Test
    fun categories_areValid() {
        val books = InitialBookData.getSampleBooks()
        val categories = books.map { it.category }.distinct()
        assertTrue(categories.contains("Asoosama"))
        assertTrue(categories.contains("Seenaa"))
        assertTrue(categories.contains("Walaloo"))
    }

    @Test
    fun importedPdfResult_creationAndFields() {
        val pdfResult = com.example.util.ImportedPdfResult(
            title = "Aadaa fi Duudhaa Oromoo",
            author = "Fe'amaa (Uploaded by You)",
            fileSizeMb = 3.4,
            pagesCount = 119,
            content = "# Boqonnaa 1: Seensa\nAadaa boonsaa.",
            localFilePath = "/data/user/0/com.example/files/pdfs/test.pdf"
        )
        assertEquals("Aadaa fi Duudhaa Oromoo", pdfResult.title)
        assertEquals(3.4, pdfResult.fileSizeMb, 0.01)
        assertEquals(119, pdfResult.pagesCount)
        assertTrue(pdfResult.content.contains("Boqonnaa 1"))
    }
}
