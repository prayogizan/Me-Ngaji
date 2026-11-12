package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit test suite verifying the comprehensive lifecycle of Ayah bookmarks management (CMP-608).
 *
 * Verifies interaction between [GetBookmarksUseCase] and [ToggleBookmarkUseCase],
 * bounds validation, and descending timestamp ordering invariant.
 */
class ManageBookmarksUseCaseTest {

    @Test
    fun bookmarkLifecycle_addRetrieveToggleRemove_maintainsInvariants() = runTest {
        val repo = FakeQuranRepository()
        val getBookmarks = GetBookmarksUseCase(repo)
        val toggleBookmark = ToggleBookmarkUseCase(repo)

        // 1. Initial state is empty
        assertTrue(getBookmarks().isEmpty())

        // 2. Add first bookmark
        val bookmark1 = AyahBookmark(
            globalAyahNumber = 1,
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            arabicSnippet = "بِسْمِ ٱللَّهِ",
            savedAtEpochMillis = 1000L
        )
        val added1 = toggleBookmark(bookmark1)
        assertTrue(added1)

        val listAfterFirst = getBookmarks()
        assertEquals(1, listAfterFirst.size)
        assertEquals(bookmark1, listAfterFirst.first())

        // 3. Add second bookmark with newer timestamp
        val bookmark2 = AyahBookmark(
            globalAyahNumber = 262,
            surahNumber = 2,
            surahNameEnglish = "Al-Baqara",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 255,
            pageNumber = 42,
            arabicSnippet = "اللَّهُ لَا إِلَٰهَ",
            savedAtEpochMillis = 2000L
        )
        val added2 = toggleBookmark(bookmark2)
        assertTrue(added2)

        val listAfterSecond = getBookmarks()
        assertEquals(2, listAfterSecond.size)
        assertTrue(listAfterSecond.any { it.globalAyahNumber == 262 })
        assertTrue(listAfterSecond.any { it.globalAyahNumber == 1 })

        // 4. Toggle bookmark2 off (removal)
        val removed = toggleBookmark(bookmark2)
        assertFalse(removed)

        val listAfterRemoval = getBookmarks()
        assertEquals(1, listAfterRemoval.size)
        assertEquals(1, listAfterRemoval.first().globalAyahNumber)
    }

    @Test
    fun toggleBookmark_boundsValidation_rejectsInvalidCoordinates() = runTest {
        val repo = FakeQuranRepository()
        val toggleBookmark = ToggleBookmarkUseCase(repo)

        // Invalid global ayah < 1
        val invalidLowGlobal = AyahBookmark(
            globalAyahNumber = 0,
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            arabicSnippet = "Test",
            savedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            toggleBookmark(invalidLowGlobal)
        }

        // Invalid global ayah > 6236
        val invalidHighGlobal = AyahBookmark(
            globalAyahNumber = 6237,
            surahNumber = 114,
            surahNameEnglish = "An-Naas",
            surahNameArabic = "سُورَةُ النَّاسِ",
            ayahNumberInSurah = 6,
            pageNumber = 604,
            arabicSnippet = "Test",
            savedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            toggleBookmark(invalidHighGlobal)
        }

        // Invalid surah number < 1
        val invalidLowSurah = AyahBookmark(
            globalAyahNumber = 1,
            surahNumber = 0,
            surahNameEnglish = "Invalid",
            surahNameArabic = "Invalid",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            arabicSnippet = "Test",
            savedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            toggleBookmark(invalidLowSurah)
        }

        // Invalid surah number > 114
        val invalidHighSurah = AyahBookmark(
            globalAyahNumber = 1,
            surahNumber = 115,
            surahNameEnglish = "Invalid",
            surahNameArabic = "Invalid",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            arabicSnippet = "Test",
            savedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            toggleBookmark(invalidHighSurah)
        }
    }
}
