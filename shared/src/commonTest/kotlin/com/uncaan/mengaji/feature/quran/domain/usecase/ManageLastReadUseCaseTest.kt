package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Unit test suite verifying the comprehensive lifecycle of recitation progress tracking (CMP-608).
 *
 * Verifies interaction between [GetLastReadUseCase] and [SaveLastReadUseCase],
 * bounds validation across Surah, Page, and Ayah coordinates, and mode switching.
 */
class ManageLastReadUseCaseTest {

    @Test
    fun lastReadLifecycle_saveRetrieveUpdate_preservesStateAcrossModes() = runTest {
        val repo = FakeQuranRepository()
        val getLastRead = GetLastReadUseCase(repo)
        val saveLastRead = SaveLastReadUseCase(repo)

        // 1. Initial state is null
        assertNull(getLastRead())

        // 2. Save progress in VERSE_LIST mode
        val progressVerseList = LastReadProgress(
            surahNumber = 18,
            surahNameEnglish = "Al-Kahf",
            surahNameArabic = "سُورَةُ الكَهْفِ",
            ayahNumberInSurah = 10,
            pageNumber = 294,
            juzNumber = 15,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1730000000000L
        )
        saveLastRead(progressVerseList)

        val retrieved1 = getLastRead()
        assertEquals(progressVerseList, retrieved1)
        assertEquals(ReaderMode.VERSE_LIST, retrieved1?.preferredReaderMode)

        // 3. Update progress to MUSHAF_PAGE mode
        val progressMushafPage = LastReadProgress(
            surahNumber = 18,
            surahNameEnglish = "Al-Kahf",
            surahNameArabic = "سُورَةُ الكَهْفِ",
            ayahNumberInSurah = 27,
            pageNumber = 297,
            juzNumber = 15,
            preferredReaderMode = ReaderMode.MUSHAF_PAGE,
            updatedAtEpochMillis = 1730000050000L
        )
        saveLastRead(progressMushafPage)

        val retrieved2 = getLastRead()
        assertEquals(progressMushafPage, retrieved2)
        assertEquals(ReaderMode.MUSHAF_PAGE, retrieved2?.preferredReaderMode)
        assertEquals(297, retrieved2?.pageNumber)
    }

    @Test
    fun saveLastRead_boundsValidation_rejectsInvalidCoordinates() = runTest {
        val repo = FakeQuranRepository()
        val saveLastRead = SaveLastReadUseCase(repo)

        // Invalid surah number < 1
        val invalidLowSurah = LastReadProgress(
            surahNumber = 0,
            surahNameEnglish = "Invalid",
            surahNameArabic = "Invalid",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            juzNumber = 1,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            saveLastRead(invalidLowSurah)
        }

        // Invalid surah number > 114
        val invalidHighSurah = LastReadProgress(
            surahNumber = 115,
            surahNameEnglish = "Invalid",
            surahNameArabic = "Invalid",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            juzNumber = 1,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            saveLastRead(invalidHighSurah)
        }

        // Invalid page number < 1
        val invalidLowPage = LastReadProgress(
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 1,
            pageNumber = 0,
            juzNumber = 1,
            preferredReaderMode = ReaderMode.MUSHAF_PAGE,
            updatedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            saveLastRead(invalidLowPage)
        }

        // Invalid page number > 604
        val invalidHighPage = LastReadProgress(
            surahNumber = 114,
            surahNameEnglish = "An-Naas",
            surahNameArabic = "سُورَةُ النَّاسِ",
            ayahNumberInSurah = 1,
            pageNumber = 605,
            juzNumber = 30,
            preferredReaderMode = ReaderMode.MUSHAF_PAGE,
            updatedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            saveLastRead(invalidHighPage)
        }

        // Invalid ayah number < 1
        val invalidLowAyah = LastReadProgress(
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 0,
            pageNumber = 1,
            juzNumber = 1,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1000L
        )
        assertFailsWith<IllegalArgumentException> {
            saveLastRead(invalidLowAyah)
        }
    }
}

