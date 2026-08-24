package com.uncaan.mengaji.feature.quran.data.repository

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiServiceImpl
import com.uncaan.mengaji.testutil.createMockHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuranRepositoryTest {

    private val sampleSuccessMultiEditionJson = """
        {
          "code": 200,
          "status": "OK",
          "data": [
            {
              "number": 262,
              "text": "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
              "edition": {
                "identifier": "quran-uthmani",
                "language": "ar",
                "name": "Uthmani",
                "englishName": "Uthmani",
                "format": "text",
                "type": "quran"
              },
              "surah": {
                "number": 2,
                "name": "سُورَةُ البَقَرَةِ",
                "englishName": "Al-Baqara",
                "englishNameTranslation": "The Cow",
                "numberOfAyahs": 286,
                "revelationType": "Medinan"
              },
              "numberInSurah": 255,
              "juz": 3
            },
            {
              "number": 262,
              "text": "Allah! There is no deity except Him, the Ever-Living, the Sustainer of existence.",
              "edition": {
                "identifier": "en.sahih",
                "language": "en",
                "name": "Saheeh International",
                "englishName": "Saheeh International",
                "format": "text",
                "type": "translation"
              },
              "surah": {
                "number": 2,
                "name": "سُورَةُ البَقَرَةِ",
                "englishName": "Al-Baqara",
                "englishNameTranslation": "The Cow",
                "numberOfAyahs": 286,
                "revelationType": "Medinan"
              },
              "numberInSurah": 255,
              "juz": 3
            },
            {
              "number": 262,
              "text": "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
              "audio": "https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3",
              "edition": {
                "identifier": "ar.alafasy",
                "language": "ar",
                "name": "Alafasy",
                "englishName": "Alafasy",
                "format": "audio",
                "type": "versebyverse"
              },
              "surah": {
                "number": 2,
                "name": "سُورَةُ البَقَرَةِ",
                "englishName": "Al-Baqara",
                "englishNameTranslation": "The Cow",
                "numberOfAyahs": 286,
                "revelationType": "Medinan"
              },
              "numberInSurah": 255,
              "juz": 3
            }
          ]
        }
    """.trimIndent()

    @Test
    fun getAyah_success_returnsMappedDomainModel() = runTest {
        val client = createMockHttpClient(sampleSuccessMultiEditionJson)
        val apiService = QuranApiServiceImpl(client)
        val repository = QuranRepositoryImpl(apiService)

        val result = repository.getAyah("2:255", "en.sahih", "ar.alafasy")

        assertTrue(result is AppResult.Success)
        val ayah = result.data
        assertEquals(262, ayah.number)
        assertEquals("اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ", ayah.text)
        assertEquals("Allah! There is no deity except Him, the Ever-Living, the Sustainer of existence.", ayah.translation)
        assertEquals("https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3", ayah.audioUrl)
        assertEquals(2, ayah.surahNumber)
        assertEquals("Al-Baqara", ayah.surahName)
        assertEquals("سُورَةُ البَقَرَةِ", ayah.surahArabicName)
        assertEquals("The Cow", ayah.englishNameTranslation)
        assertEquals(255, ayah.numberInSurah)
        assertEquals(3, ayah.juz)
    }

    @Test
    fun getAyah_404_returnsNotFoundErrorResult() = runTest {
        val client = createMockHttpClient(
            responseJson = """{"code": 404, "status": "Not Found", "data": "Ayah not found"}""",
            statusCode = HttpStatusCode.NotFound
        )
        val apiService = QuranApiServiceImpl(client)
        val repository = QuranRepositoryImpl(apiService)

        val result = repository.getAyah("999:999", "en.sahih", "ar.alafasy")

        assertTrue(result is AppResult.Error)
        assertEquals("Requested resource not found", result.message)
    }

    @Test
    fun getAyah_networkError_returnsNetworkErrorResult() = runTest {
        val mockEngine = MockEngine {
            throw IOException("Network connection error")
        }
        val client = HttpClient(mockEngine) {
            expectSuccess = true
            defaultRequest { url("https://api.alquran.cloud/v1/") }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
        val apiService = QuranApiServiceImpl(client)
        val repository = QuranRepositoryImpl(apiService)

        val result = repository.getAyah("1:1", "en.sahih", "ar.alafasy")

        assertTrue(result is AppResult.Error)
        assertEquals("No internet connection or network timeout.", result.message)
    }
}
