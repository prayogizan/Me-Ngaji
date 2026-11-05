package com.uncaan.mengaji.feature.quran.data.repository

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiServiceImpl
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import com.uncaan.mengaji.testutil.createMockHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
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
              "juz": 3,
              "page": 42,
              "hizbQuarter": 17
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
              "juz": 3,
              "page": 42,
              "hizbQuarter": 17
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
              "juz": 3,
              "page": 42,
              "hizbQuarter": 17
            }
          ]
        }
    """.trimIndent()

    private val sampleSurahListJson = """
        {
          "code": 200,
          "status": "OK",
          "data": [
            {
              "number": 1,
              "name": "سُورَةُ ٱلْفَاتِحَةِ",
              "englishName": "Al-Faatiha",
              "englishNameTranslation": "The Opening",
              "numberOfAyahs": 7,
              "revelationType": "Meccan"
            },
            {
              "number": 2,
              "name": "سُورَةُ البَقَرَةِ",
              "englishName": "Al-Baqara",
              "englishNameTranslation": "The Cow",
              "numberOfAyahs": 286,
              "revelationType": "Medinan"
            }
          ]
        }
    """.trimIndent()

    private val sampleSurahDetailJson = """
        {
          "code": 200,
          "status": "OK",
          "data": [
            {
              "number": 1,
              "name": "سُورَةُ ٱلْفَاتِحَةِ",
              "englishName": "Al-Faatiha",
              "englishNameTranslation": "The Opening",
              "revelationType": "Meccan",
              "numberOfAyahs": 7,
              "ayahs": [
                {
                  "number": 1,
                  "text": "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                  "numberInSurah": 1,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                }
              ],
              "edition": {
                "identifier": "quran-uthmani",
                "language": "ar",
                "name": "Uthmani",
                "englishName": "Uthmani",
                "format": "text",
                "type": "quran"
              }
            },
            {
              "number": 1,
              "name": "سُورَةُ ٱلْفَاتِحَةِ",
              "englishName": "Al-Faatiha",
              "englishNameTranslation": "The Opening",
              "revelationType": "Meccan",
              "numberOfAyahs": 7,
              "ayahs": [
                {
                  "number": 1,
                  "text": "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                  "numberInSurah": 1,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                }
              ],
              "edition": {
                "identifier": "en.sahih",
                "language": "en",
                "name": "Saheeh International",
                "englishName": "Saheeh International",
                "format": "text",
                "type": "translation"
              }
            },
            {
              "number": 1,
              "name": "سُورَةُ ٱلْفَاتِحَةِ",
              "englishName": "Al-Faatiha",
              "englishNameTranslation": "The Opening",
              "revelationType": "Meccan",
              "numberOfAyahs": 7,
              "ayahs": [
                {
                  "number": 1,
                  "text": "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                  "audio": "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
                  "numberInSurah": 1,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                }
              ],
              "edition": {
                "identifier": "ar.alafasy",
                "language": "ar",
                "name": "Alafasy",
                "englishName": "Alafasy",
                "format": "audio",
                "type": "versebyverse"
              }
            }
          ]
        }
    """.trimIndent()

    private val sampleMushafPageJson = """
        {
          "code": 200,
          "status": "OK",
          "data": [
            {
              "number": 1,
              "ayahs": [
                {
                  "number": 1,
                  "text": "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                  "surah": {
                    "number": 1,
                    "name": "سُورَةُ ٱلْفَاتِحَةِ",
                    "englishName": "Al-Faatiha",
                    "englishNameTranslation": "The Opening",
                    "revelationType": "Meccan",
                    "numberOfAyahs": 7
                  },
                  "numberInSurah": 1,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                }
              ],
              "surahs": {
                "1": {
                  "number": 1,
                  "name": "سُورَةُ ٱلْفَاتِحَةِ",
                  "englishName": "Al-Faatiha",
                  "englishNameTranslation": "The Opening",
                  "revelationType": "Meccan",
                  "numberOfAyahs": 7
                }
              },
              "edition": {
                "identifier": "quran-uthmani",
                "language": "ar",
                "name": "Uthmani",
                "englishName": "Uthmani",
                "format": "text",
                "type": "quran"
              }
            },
            {
              "number": 1,
              "ayahs": [
                {
                  "number": 1,
                  "text": "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                  "surah": {
                    "number": 1,
                    "name": "سُورَةُ ٱلْفَاتِحَةِ",
                    "englishName": "Al-Faatiha",
                    "englishNameTranslation": "The Opening",
                    "revelationType": "Meccan",
                    "numberOfAyahs": 7
                  },
                  "numberInSurah": 1,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                }
              ],
              "surahs": {
                "1": {
                  "number": 1,
                  "name": "سُورَةُ ٱلْفَاتِحَةِ",
                  "englishName": "Al-Faatiha",
                  "englishNameTranslation": "The Opening",
                  "revelationType": "Meccan",
                  "numberOfAyahs": 7
                }
              },
              "edition": {
                "identifier": "en.sahih",
                "language": "en",
                "name": "Saheeh International",
                "englishName": "Saheeh International",
                "format": "text",
                "type": "translation"
              }
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
        assertEquals(42, ayah.page)
        assertEquals(17, ayah.hizbQuarter)
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

    @Test
    fun getSurahList_success_mapsCatalogAndCachesUntilForceRefresh() = runTest {
        var networkCallCount = 0
        val mockEngine = MockEngine {
            networkCallCount++
            respond(
                content = sampleSurahListJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
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

        // First call hits network
        val firstResult = repository.getSurahList(forceRefresh = false)
        assertTrue(firstResult is AppResult.Success)
        assertEquals(2, firstResult.data.size)
        assertEquals("Al-Faatiha", firstResult.data[0].englishName)
        assertEquals(RevelationType.MECCAN, firstResult.data[0].revelationType)
        assertEquals(RevelationType.MEDINAN, firstResult.data[1].revelationType)
        assertEquals(1, networkCallCount)

        // Second call with forceRefresh = false returns in-memory cache without network I/O
        val cachedResult = repository.getSurahList(forceRefresh = false)
        assertTrue(cachedResult is AppResult.Success)
        assertEquals(2, cachedResult.data.size)
        assertEquals(1, networkCallCount)

        // Third call with forceRefresh = true queries network again
        val refreshedResult = repository.getSurahList(forceRefresh = true)
        assertTrue(refreshedResult is AppResult.Success)
        assertEquals(2, networkCallCount)
    }

    @Test
    fun getSurahDetail_success_mergesMultiEditionAyahs() = runTest {
        val client = createMockHttpClient(sampleSurahDetailJson)
        val apiService = QuranApiServiceImpl(client)
        val repository = QuranRepositoryImpl(apiService)

        val result = repository.getSurahDetail(
            surahNumber = 1,
            translationEdition = "en.sahih",
            audioEdition = "ar.alafasy"
        )

        assertTrue(result is AppResult.Success)
        val detail = result.data
        assertEquals(1, detail.number)
        assertEquals("Al-Faatiha", detail.englishName)
        assertEquals("The Opening", detail.englishNameTranslation)
        assertEquals(RevelationType.MECCAN, detail.revelationType)
        assertEquals(7, detail.numberOfAyahs)
        assertEquals(1, detail.ayahs.size)

        val ayah = detail.ayahs[0]
        assertEquals(1, ayah.number)
        assertEquals("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", ayah.text)
        assertEquals("In the name of Allah, the Entirely Merciful, the Especially Merciful.", ayah.translation)
        assertEquals("https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3", ayah.audioUrl)
        assertEquals(1, ayah.surahNumber)
        assertEquals("Al-Faatiha", ayah.surahName)
    }

    @Test
    fun getMushafPage_success_mergesMultiEditionPageVersesAndSurahs() = runTest {
        val client = createMockHttpClient(sampleMushafPageJson)
        val apiService = QuranApiServiceImpl(client)
        val repository = QuranRepositoryImpl(apiService)

        val result = repository.getMushafPage(
            pageNumber = 1,
            translationEdition = "en.sahih"
        )

        assertTrue(result is AppResult.Success)
        val page = result.data
        assertEquals(1, page.pageNumber)
        assertEquals(1, page.juzNumber)
        assertEquals(1, page.hizbQuarter)
        assertEquals("en.sahih", page.translationEdition)
        assertEquals(1, page.surahs.size)
        assertEquals("Al-Faatiha", page.surahs[0].englishName)
        assertEquals(1, page.ayahs.size)
        assertEquals("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", page.ayahs[0].text)
        assertEquals("In the name of Allah, the Entirely Merciful, the Especially Merciful.", page.ayahs[0].translation)
    }
}
