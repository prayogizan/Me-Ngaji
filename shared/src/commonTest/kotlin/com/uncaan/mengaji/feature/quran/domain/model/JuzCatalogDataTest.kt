package com.uncaan.mengaji.feature.quran.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JuzCatalogDataTest {

    @Test
    fun allJuz_containsExactlyThirtyJuz() {
        assertEquals(30, JuzCatalogData.allJuz.size)
    }

    @Test
    fun allJuz_hasStrictSequentialNumberingFromOneToThirty() {
        JuzCatalogData.allJuz.forEachIndexed { index, juz ->
            assertEquals(index + 1, juz.number)
        }
    }

    @Test
    fun allJuz_pageRangesCoverComplete604PageMedinaMushafWithoutGaps() {
        assertEquals(1, JuzCatalogData.allJuz.first().startPageNumber)
        assertEquals(604, JuzCatalogData.allJuz.last().endPageNumber)

        for (i in 0 until JuzCatalogData.allJuz.size - 1) {
            val currentJuz = JuzCatalogData.allJuz[i]
            val nextJuz = JuzCatalogData.allJuz[i + 1]
            assertEquals(
                expected = currentJuz.endPageNumber + 1,
                actual = nextJuz.startPageNumber,
                message = "Gap detected between Juz ${currentJuz.number} and Juz ${nextJuz.number}"
            )
        }
    }

    @Test
    fun getJuzByNumber_returnsCorrectJuzOrNull() {
        val juz1 = JuzCatalogData.getJuzByNumber(1)
        assertNotNull(juz1)
        assertEquals("Al-Fatihah", juz1.startSurahNameEnglish)
        assertEquals(1, juz1.startSurahNumber)

        val juz30 = JuzCatalogData.getJuzByNumber(30)
        assertNotNull(juz30)
        assertEquals("An-Naba", juz30.startSurahNameEnglish)
        assertEquals("An-Nas", juz30.endSurahNameEnglish)

        assertNull(JuzCatalogData.getJuzByNumber(0))
        assertNull(JuzCatalogData.getJuzByNumber(31))
    }

    @Test
    fun getJuzForPage_mapsPagesToExpectedJuz() {
        // Page 1 is in Juz 1
        assertEquals(1, JuzCatalogData.getJuzForPage(1).number)
        // Page 21 is in Juz 1
        assertEquals(1, JuzCatalogData.getJuzForPage(21).number)
        // Page 22 is in Juz 2
        assertEquals(2, JuzCatalogData.getJuzForPage(22).number)
        // Page 42 (Al-Baqarah 255) is in Juz 3
        assertEquals(3, JuzCatalogData.getJuzForPage(42).number)
        // Page 604 is in Juz 30
        assertEquals(30, JuzCatalogData.getJuzForPage(604).number)
    }

    @Test
    fun allJuz_totalVersesArePositiveAndValid() {
        JuzCatalogData.allJuz.forEach { juz ->
            assertTrue(juz.totalVerses > 0, "Juz ${juz.number} has non-positive verse count")
            assertTrue(juz.nameArabic.isNotBlank(), "Juz ${juz.number} has blank Arabic name")
        }
    }
}

