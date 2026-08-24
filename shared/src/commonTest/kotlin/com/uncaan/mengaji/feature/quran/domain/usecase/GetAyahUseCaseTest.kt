package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeQuranRepository(
    private val shouldReturnError: Boolean = false
) : QuranRepository {
    override suspend fun getAyah(reference: String, edition: String): AppResult<Ayah> {
        if (shouldReturnError) return AppResult.Error(Exception("Network failure"))
        return AppResult.Success(
            Ayah(
                number = 1,
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
                surahNumber = 1,
                surahName = "Al-Fatiha",
                numberInSurah = 1,
                juz = 1
            )
        )
    }
}

class GetAyahUseCaseTest {

    @Test
    fun invoke_emptyReference_returnsErrorWithoutCallingRepo() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetAyahUseCase(repo)

        val result = useCase("   ")

        assertTrue(result is AppResult.Error)
        assertEquals("Ayah reference cannot be empty", result.message)
    }

    @Test
    fun invoke_validReference_returnsSuccess() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetAyahUseCase(repo)

        val result = useCase("1:1")

        assertTrue(result is AppResult.Success)
        val ayah = result.data
        assertEquals(1, ayah.number)
        assertEquals("Al-Fatiha", ayah.surahName)
    }
}
