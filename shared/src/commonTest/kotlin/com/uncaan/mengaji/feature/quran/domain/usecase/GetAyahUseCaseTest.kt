package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeQuranRepository(
    private val shouldReturnError: Boolean = false
) : QuranRepository {

    var getSurahListCallCount: Int = 0
    var lastForceRefresh: Boolean? = null
    var getSurahDetailCallCount: Int = 0
    var lastSurahNumber: Int? = null
    var lastTranslationEdition: String? = null
    var lastAudioEdition: String? = null
    var getMushafPageCallCount: Int = 0
    var lastPageNumber: Int? = null

    override suspend fun getAyah(
        reference: String,
        translationEdition: String,
        recitationEdition: String
    ): AppResult<Ayah> {
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
                juz = 1,
                page = 1,
                hizbQuarter = 1
            )
        )
    }

    override suspend fun getSurahList(
        forceRefresh: Boolean
    ): AppResult<List<SurahSummary>> {
        getSurahListCallCount++
        lastForceRefresh = forceRefresh
        if (shouldReturnError) return AppResult.Error(Exception("Network failure"))
        return AppResult.Success(
            listOf(
                SurahSummary(
                    number = 1,
                    name = "سُورَةُ ٱلْفَاتِحَةِ",
                    englishName = "Al-Faatiha",
                    englishNameTranslation = "The Opening",
                    numberOfAyahs = 7,
                    revelationType = RevelationType.MECCAN
                ),
                SurahSummary(
                    number = 2,
                    name = "سُورَةُ البَقَرَةِ",
                    englishName = "Al-Baqara",
                    englishNameTranslation = "The Cow",
                    numberOfAyahs = 286,
                    revelationType = RevelationType.MEDINAN
                )
            )
        )
    }

    override suspend fun getSurahDetail(
        surahNumber: Int,
        translationEdition: String,
        audioEdition: String
    ): AppResult<SurahDetail> {
        getSurahDetailCallCount++
        lastSurahNumber = surahNumber
        lastTranslationEdition = translationEdition
        lastAudioEdition = audioEdition
        if (shouldReturnError) return AppResult.Error(Exception("Network failure"))
        return AppResult.Success(
            SurahDetail(
                number = surahNumber,
                name = "سُورَةُ ٱلْفَاتِحَةِ",
                englishName = "Al-Faatiha",
                englishNameTranslation = "The Opening",
                revelationType = RevelationType.MECCAN,
                numberOfAyahs = 7,
                ayahs = listOf(
                    Ayah(
                        number = 1,
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                        translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                        audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
                        surahNumber = surahNumber,
                        surahName = "Al-Faatiha",
                        surahArabicName = "سُورَةُ ٱلْفَاتِحَةِ",
                        englishNameTranslation = "The Opening",
                        numberInSurah = 1,
                        juz = 1,
                        page = 1,
                        hizbQuarter = 1
                    )
                ),
                translationEdition = translationEdition,
                audioEdition = audioEdition
            )
        )
    }

    override suspend fun getMushafPage(
        pageNumber: Int,
        translationEdition: String
    ): AppResult<MushafPage> {
        getMushafPageCallCount++
        lastPageNumber = pageNumber
        lastTranslationEdition = translationEdition
        if (shouldReturnError) return AppResult.Error(Exception("Network failure"))
        return AppResult.Success(
            MushafPage(
                pageNumber = pageNumber,
                ayahs = listOf(
                    Ayah(
                        number = 1,
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                        translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                        audioUrl = null,
                        surahNumber = 1,
                        surahName = "Al-Faatiha",
                        surahArabicName = "سُورَةُ ٱلْفَاتِحَةِ",
                        englishNameTranslation = "The Opening",
                        numberInSurah = 1,
                        juz = 1,
                        page = pageNumber,
                        hizbQuarter = 1
                    )
                ),
                surahs = listOf(
                    SurahSummary(
                        number = 1,
                        name = "سُورَةُ ٱلْفَاتِحَةِ",
                        englishName = "Al-Faatiha",
                        englishNameTranslation = "The Opening",
                        numberOfAyahs = 7,
                        revelationType = RevelationType.MECCAN
                    )
                ),
                juzNumber = 1,
                hizbQuarter = 1,
                translationEdition = translationEdition
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
