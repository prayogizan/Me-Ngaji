package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SaveLastReadUseCaseTest {

    @Test
    fun invoke_validProgress_delegatesToRepository() = runTest {
        val repo = FakeQuranRepository()
        val useCase = SaveLastReadUseCase(repo)

        val progress = LastReadProgress(
            surahNumber = 2,
            surahNameEnglish = "Al-Baqara",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 255,
            pageNumber = 42,
            juzNumber = 3,
            preferredReaderMode = ReaderMode.MUSHAF_PAGE,
            updatedAtEpochMillis = 1730000000000L
        )

        useCase(progress)

        assertEquals(progress, repo.lastReadProgress)
    }

    @Test
    fun invoke_invalidSurahNumber_throwsIllegalArgumentException() = runTest {
        val repo = FakeQuranRepository()
        val useCase = SaveLastReadUseCase(repo)

        val progress = LastReadProgress(
            surahNumber = 115,
            surahNameEnglish = "Invalid",
            surahNameArabic = "Invalid",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            juzNumber = 1,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1730000000000L
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(progress)
        }
    }

    @Test
    fun invoke_invalidPageNumber_throwsIllegalArgumentException() = runTest {
        val repo = FakeQuranRepository()
        val useCase = SaveLastReadUseCase(repo)

        val progress = LastReadProgress(
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 1,
            pageNumber = 605,
            juzNumber = 30,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1730000000000L
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(progress)
        }
    }

    @Test
    fun invoke_invalidAyahNumber_throwsIllegalArgumentException() = runTest {
        val repo = FakeQuranRepository()
        val useCase = SaveLastReadUseCase(repo)

        val progress = LastReadProgress(
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 0,
            pageNumber = 1,
            juzNumber = 1,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1730000000000L
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(progress)
        }
    }
}

