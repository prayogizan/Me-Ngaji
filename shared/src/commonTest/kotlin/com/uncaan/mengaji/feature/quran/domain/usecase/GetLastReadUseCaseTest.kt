package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetLastReadUseCaseTest {

    @Test
    fun invoke_whenNoProgressSaved_returnsNull() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetLastReadUseCase(repo)

        val result = useCase()

        assertNull(result)
    }

    @Test
    fun invoke_whenProgressSaved_returnsSavedProgress() = runTest {
        val repo = FakeQuranRepository()
        val expected = LastReadProgress(
            surahNumber = 18,
            surahNameEnglish = "Al-Kahf",
            surahNameArabic = "سُورَةُ الكَهْفِ",
            ayahNumberInSurah = 1,
            pageNumber = 293,
            juzNumber = 15,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1730000000000L
        )
        repo.lastReadProgress = expected
        val useCase = GetLastReadUseCase(repo)

        val result = useCase()

        assertEquals(expected, result)
    }
}

