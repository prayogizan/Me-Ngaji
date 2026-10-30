package com.uncaan.mengaji.feature.widget.data.repository

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.widget.data.remote.DailyAyahApiServiceImpl
import com.uncaan.mengaji.testutil.createMockHttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DailyAyahRepositoryTest {

    private val sampleMultiEditionResponseJson = """
        {
          "code": 200,
          "status": "OK",
          "data": [
            {
              "number": 3644,
              "text": "وَٱلَّذِينَ يَسْعَوْنَ فِىٓ ءَايَٰتِنَا مُعَٰجِزِينَ أُو۟لَٰٓئِكَ فِى ٱلْعَذَابِ مُحْضَرُونَ",
              "edition": {
                "identifier": "quran-uthmani",
                "language": "ar",
                "name": "القرآن الكريم برسم العثماني (uthmani)",
                "englishName": "Uthmani",
                "format": "text",
                "type": "quran",
                "direction": "rtl"
              },
              "surah": {
                "number": 34,
                "name": "سُورَةُ سَبَإٍ",
                "englishName": "Saba",
                "englishNameTranslation": "Sheba",
                "numberOfAyahs": 54,
                "revelationType": "Meccan"
              },
              "numberInSurah": 38,
              "juz": 22
            },
            {
              "number": 3644,
              "text": "Dan orang-orang yang berusaha menentang ayat-ayat Kami untuk melemahkan (kemauan untuk beriman), mereka itu dimasukkan ke dalam azab.",
              "edition": {
                "identifier": "id.indonesian",
                "language": "id",
                "name": "Indonesian Ministry of Religious Affairs",
                "englishName": "Indonesian",
                "format": "text",
                "type": "translation",
                "direction": "ltr"
              },
              "surah": {
                "number": 34,
                "name": "سُورَةُ سَبَإٍ",
                "englishName": "Saba",
                "englishNameTranslation": "Sheba",
                "numberOfAyahs": 54,
                "revelationType": "Meccan"
              },
              "numberInSurah": 38,
              "juz": 22
            }
          ]
        }
    """.trimIndent()

    @Test
    fun getDailyAyah_success_returnsMappedDailyAyah() = runTest {
        val httpClient = createMockHttpClient(sampleMultiEditionResponseJson, HttpStatusCode.OK)
        val apiService = DailyAyahApiServiceImpl(httpClient)
        val repository = DailyAyahRepositoryImpl(
            apiService = apiService,
            ioDispatcher = Dispatchers.Unconfined,
            currentDateProvider = { "2026-10-22" }
        )

        val result = repository.getDailyAyah()

        assertTrue(result is AppResult.Success)
        val ayah = result.data
        assertEquals("وَٱلَّذِينَ يَسْعَوْنَ فِىٓ ءَايَٰتِنَا مُعَٰجِزِينَ أُو۟لَٰٓئِكَ فِى ٱلْعَذَابِ مُحْضَرُونَ", ayah.arabicText)
        assertEquals(34, ayah.surahNumber)
        assertEquals("Saba", ayah.surahName)
        assertEquals("سُورَةُ سَبَإٍ", ayah.surahNameArabic)
        assertEquals(38, ayah.ayahNumberInSurah)
        assertEquals(3644, ayah.globalAyahNumber)
        assertEquals("id.indonesian", ayah.translationEdition)
        assertEquals("2026-10-22", ayah.fetchedDate)
    }

    @Test
    fun getDailyAyah_englishEdition_returnsMappedEnglishAyah() = runTest {
        val englishResponseJson = sampleMultiEditionResponseJson
            .replace("\"identifier\": \"id.indonesian\"", "\"identifier\": \"en.asad\"")
            .replace(
                "Dan orang-orang yang berusaha menentang ayat-ayat Kami untuk melemahkan (kemauan untuk beriman), mereka itu dimasukkan ke dalam azab.",
                "Whereas those who strive against Our messages..."
            )

        val httpClient = createMockHttpClient(englishResponseJson, HttpStatusCode.OK)
        val apiService = DailyAyahApiServiceImpl(httpClient)
        val repository = DailyAyahRepositoryImpl(
            apiService = apiService,
            ioDispatcher = Dispatchers.Unconfined,
            currentDateProvider = { "2026-10-30" }
        )

        val result = repository.getDailyAyah(edition = "en.asad")

        assertTrue(result is AppResult.Success)
        val ayah = result.data
        assertEquals("en.asad", ayah.translationEdition)
        assertEquals("Whereas those who strive against Our messages...", ayah.translationText)
    }

    @Test
    fun getDailyAyah_notFound_returnsAppResultError() = runTest {
        val httpClient = createMockHttpClient("{}", HttpStatusCode.NotFound)
        val apiService = DailyAyahApiServiceImpl(httpClient)
        val repository = DailyAyahRepositoryImpl(
            apiService = apiService,
            ioDispatcher = Dispatchers.Unconfined
        )

        val result = repository.getDailyAyah()

        assertTrue(result is AppResult.Error)
        assertEquals("Requested resource not found", result.message)
    }

    @Test
    fun getDailyAyah_serverError_returnsAppResultError() = runTest {
        val httpClient = createMockHttpClient("{}", HttpStatusCode.InternalServerError)
        val apiService = DailyAyahApiServiceImpl(httpClient)
        val repository = DailyAyahRepositoryImpl(
            apiService = apiService,
            ioDispatcher = Dispatchers.Unconfined
        )

        val result = repository.getDailyAyah()

        assertTrue(result is AppResult.Error)
        assertEquals("Server error. Please try again later.", result.message)
    }
}
