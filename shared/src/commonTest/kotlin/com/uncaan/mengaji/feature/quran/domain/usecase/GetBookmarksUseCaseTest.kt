package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetBookmarksUseCaseTest {

    @Test
    fun invoke_whenEmpty_returnsEmptyList() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetBookmarksUseCase(repo)

        val result = useCase()

        assertTrue(result.isEmpty())
    }

    @Test
    fun invoke_whenBookmarksExist_returnsBookmarksList() = runTest {
        val repo = FakeQuranRepository()
        val bookmark = AyahBookmark(
            globalAyahNumber = 1,
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            arabicSnippet = "بِسْمِ ٱللَّهِ",
            savedAtEpochMillis = 1730000000000L
        )
        repo.bookmarksList.add(bookmark)
        val useCase = GetBookmarksUseCase(repo)

        val result = useCase()

        assertEquals(1, result.size)
        assertEquals(bookmark, result.first())
    }
}

