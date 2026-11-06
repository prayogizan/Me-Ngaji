package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ToggleBookmarkUseCaseTest {

    @Test
    fun invoke_validBookmark_delegatesToRepository() = runTest {
        val repo = FakeQuranRepository()
        val useCase = ToggleBookmarkUseCase(repo)

        val bookmark = AyahBookmark(
            globalAyahNumber = 262,
            surahNumber = 2,
            surahNameEnglish = "Al-Baqara",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 255,
            pageNumber = 42,
            arabicSnippet = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
            savedAtEpochMillis = 1730000000000L
        )

        val added = useCase(bookmark)
        assertTrue(added)

        val removed = useCase(bookmark)
        assertFalse(removed)
    }

    @Test
    fun invoke_invalidGlobalAyahNumber_throwsIllegalArgumentException() = runTest {
        val repo = FakeQuranRepository()
        val useCase = ToggleBookmarkUseCase(repo)

        val bookmark = AyahBookmark(
            globalAyahNumber = 6237,
            surahNumber = 114,
            surahNameEnglish = "An-Naas",
            surahNameArabic = "سُورَةُ النَّاسِ",
            ayahNumberInSurah = 6,
            pageNumber = 604,
            arabicSnippet = "مِنَ ٱلْجِنَّةِ وَٱلنَّاسِ",
            savedAtEpochMillis = 1730000000000L
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(bookmark)
        }
    }

    @Test
    fun invoke_invalidSurahNumber_throwsIllegalArgumentException() = runTest {
        val repo = FakeQuranRepository()
        val useCase = ToggleBookmarkUseCase(repo)

        val bookmark = AyahBookmark(
            globalAyahNumber = 1,
            surahNumber = 0,
            surahNameEnglish = "Invalid",
            surahNameArabic = "Invalid",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            arabicSnippet = "Invalid",
            savedAtEpochMillis = 1730000000000L
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(bookmark)
        }
    }
}

