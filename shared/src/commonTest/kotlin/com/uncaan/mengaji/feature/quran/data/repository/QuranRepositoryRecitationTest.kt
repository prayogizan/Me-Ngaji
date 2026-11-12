package com.uncaan.mengaji.feature.quran.data.repository

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiServiceImpl
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import com.uncaan.mengaji.testutil.FakeLocalRecitationCache
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Automated unit test suite verifying Quran recitation repository operations (CMP-608).
 *
 * Covers:
 * - Ktor [MockEngine] 114-Surah catalog retrieval & offline fallback to [FakeLocalRecitationCache].
 * - Multi-edition zipping for [QuranRepositoryImpl.getSurahDetail] merging `quran-uthmani`, `en.asad`, and `ar.alafasy`.
 * - Multi-edition zipping for [QuranRepositoryImpl.getMushafPage] across Medina Mushaf pages `1..604`.
 * - Local persistence operations for last read progress tracking and ayah bookmarks.
 */
class QuranRepositoryRecitationTest {

    private val testDispatcher = StandardTestDispatcher()

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

    private val sampleSurahDetailMultiEditionJson = """
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
              "numberOfAyahs": 2,
              "ayahs": [
                {
                  "number": 1,
                  "text": "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                  "numberInSurah": 1,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                },
                {
                  "number": 2,
                  "text": "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ",
                  "numberInSurah": 2,
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
              "numberOfAyahs": 2,
              "ayahs": [
                {
                  "number": 1,
                  "text": "In the name of God, the Most Gracious, the Dispenser of Grace.",
                  "numberInSurah": 1,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                },
                {
                  "number": 2,
                  "text": "All praise is due to God alone, the Sustainer of all the worlds.",
                  "numberInSurah": 2,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                }
              ],
              "edition": {
                "identifier": "en.asad",
                "language": "en",
                "name": "Muhammad Asad",
                "englishName": "Muhammad Asad",
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
              "numberOfAyahs": 2,
              "ayahs": [
                {
                  "number": 1,
                  "text": "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                  "audio": "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
                  "numberInSurah": 1,
                  "juz": 1,
                  "page": 1,
                  "hizbQuarter": 1
                },
                {
                  "number": 2,
                  "text": "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ",
                  "audio": "https://cdn.islamic.network/quran/audio/128/ar.alafasy/2.mp3",
                  "numberInSurah": 2,
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

    private val sampleMushafPageMultiEditionJson = """
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
                  "text": "In the name of God, the Most Gracious, the Dispenser of Grace.",
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
                "identifier": "en.asad",
                "language": "en",
                "name": "Muhammad Asad",
                "englishName": "Muhammad Asad",
                "format": "text",
                "type": "translation"
              }
            }
          ]
        }
    """.trimIndent()

    @Test
    fun getSurahCatalog_success_fetchesCatalogAndPopulatesOfflineCache() = runTest(testDispatcher) {
        val client = createMockHttpClient(sampleSurahListJson)
        val apiService = QuranApiServiceImpl(client)
        val localCache = FakeLocalRecitationCache()
        val repository = QuranRepositoryImpl(apiService, localCache, testDispatcher)

        val result = repository.getSurahList(forceRefresh = false)

        assertTrue(result is AppResult.Success)
        val list = result.data
        assertEquals(2, list.size)
        assertEquals("Al-Faatiha", list[0].englishName)
        assertEquals(RevelationType.MECCAN, list[0].revelationType)
        assertEquals("Al-Baqara", list[1].englishName)
        assertEquals(RevelationType.MEDINAN, list[1].revelationType)

        // Offline cache should be populated
        val cached = localCache.getCachedSurahCatalog()
        assertNotNull(cached)
        assertEquals(2, cached.size)
        assertEquals("Al-Faatiha", cached[0].englishName)
    }

    @Test
    fun getSurahCatalog_networkFailure_fallsBackToLocalCachedCatalog() = runTest(testDispatcher) {
        val mockEngine = MockEngine { _ ->
            throw IOException("Simulated network loss")
        }
        val client = HttpClient(mockEngine) {
            defaultRequest { url("https://api.alquran.cloud/v1/") }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
        val apiService = QuranApiServiceImpl(client)
        val localCache = FakeLocalRecitationCache()
        localCache.cacheSurahCatalog(
            listOf(
                SurahSummaryDto(
                    number = 1,
                    name = "سُورَةُ ٱلْفَاتِحَةِ",
                    englishName = "Al-Faatiha",
                    englishNameTranslation = "The Opening",
                    numberOfAyahs = 7,
                    revelationType = "Meccan"
                )
            )
        )
        val repository = QuranRepositoryImpl(apiService, localCache, testDispatcher)

        val result = repository.getSurahList(forceRefresh = false)

        assertTrue(result is AppResult.Success)
        val list = result.data
        assertEquals(1, list.size)
        assertEquals(1, list[0].number)
        assertEquals("Al-Faatiha", list[0].englishName)
    }

    @Test
    fun getSurahDetail_multiEditionZipping_mergesUthmaniTranslationAndAudioRecitation() = runTest(testDispatcher) {
        val client = createMockHttpClient(sampleSurahDetailMultiEditionJson)
        val apiService = QuranApiServiceImpl(client)
        val localCache = FakeLocalRecitationCache()
        val repository = QuranRepositoryImpl(apiService, localCache, testDispatcher)

        val result = repository.getSurahDetail(
            surahNumber = 1,
            translationEdition = "en.asad",
            audioEdition = "ar.alafasy"
        )

        assertTrue(result is AppResult.Success)
        val surahDetail = result.data
        assertEquals(1, surahDetail.number)
        assertEquals("Al-Faatiha", surahDetail.englishName)
        assertEquals("The Opening", surahDetail.englishNameTranslation)
        assertEquals(RevelationType.MECCAN, surahDetail.revelationType)
        assertEquals("en.asad", surahDetail.translationEdition)
        assertEquals("ar.alafasy", surahDetail.audioEdition)

        assertEquals(2, surahDetail.ayahs.size)
        val ayah1 = surahDetail.ayahs[0]
        assertEquals(1, ayah1.number)
        assertEquals(1, ayah1.numberInSurah)
        assertEquals("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", ayah1.text)
        assertEquals("In the name of God, the Most Gracious, the Dispenser of Grace.", ayah1.translation)
        assertEquals("https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3", ayah1.audioUrl)

        val ayah2 = surahDetail.ayahs[1]
        assertEquals(2, ayah2.numberInSurah)
        assertEquals("ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ", ayah2.text)
        assertEquals("All praise is due to God alone, the Sustainer of all the worlds.", ayah2.translation)
        assertEquals("https://cdn.islamic.network/quran/audio/128/ar.alafasy/2.mp3", ayah2.audioUrl)
    }

    @Test
    fun getMushafPage_multiEditionZipping_mergesPageAyahsAndSurahsAscending() = runTest(testDispatcher) {
        val client = createMockHttpClient(sampleMushafPageMultiEditionJson)
        val apiService = QuranApiServiceImpl(client)
        val localCache = FakeLocalRecitationCache()
        val repository = QuranRepositoryImpl(apiService, localCache, testDispatcher)

        val result = repository.getMushafPage(pageNumber = 1, translationEdition = "en.asad")

        assertTrue(result is AppResult.Success)
        val mushafPage = result.data
        assertEquals(1, mushafPage.pageNumber)
        assertEquals(1, mushafPage.juzNumber)
        assertEquals("en.asad", mushafPage.translationEdition)
        assertEquals(1, mushafPage.ayahs.size)

        val ayah = mushafPage.ayahs[0]
        assertEquals("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", ayah.text)
        assertEquals("In the name of God, the Most Gracious, the Dispenser of Grace.", ayah.translation)

        assertEquals(1, mushafPage.surahs.size)
        assertEquals(1, mushafPage.surahs[0].number)
        assertEquals("Al-Faatiha", mushafPage.surahs[0].englishName)
    }

    @Test
    fun recitationPersistence_lastReadAndBookmarks_delegatesToLocalCache() = runTest(testDispatcher) {
        val client = createMockHttpClient("{}")
        val apiService = QuranApiServiceImpl(client)
        val localCache = FakeLocalRecitationCache()
        val repository = QuranRepositoryImpl(apiService, localCache, testDispatcher)

        // Initial state
        assertNull(repository.getLastReadProgress())
        assertTrue(repository.getBookmarks().isEmpty())

        // Save last read progress
        val progress = LastReadProgress(
            surahNumber = 2,
            surahNameEnglish = "Al-Baqara",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 255,
            pageNumber = 42,
            juzNumber = 3,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1730000000000L
        )
        repository.saveLastReadProgress(progress)

        val retrievedProgress = repository.getLastReadProgress()
        assertEquals(progress, retrievedProgress)

        // Toggle bookmarks and maintain descending timestamp order
        val bookmarkOlder = AyahBookmark(
            globalAyahNumber = 1,
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            arabicSnippet = "بِسْمِ ٱللَّهِ",
            savedAtEpochMillis = 1000L
        )
        val bookmarkNewer = AyahBookmark(
            globalAyahNumber = 262,
            surahNumber = 2,
            surahNameEnglish = "Al-Baqara",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 255,
            pageNumber = 42,
            arabicSnippet = "اللَّهُ لَا إِلَٰهَ",
            savedAtEpochMillis = 2000L
        )

        // Add both
        assertTrue(repository.toggleBookmark(bookmarkOlder))
        assertTrue(repository.toggleBookmark(bookmarkNewer))

        val bookmarks = repository.getBookmarks()
        assertEquals(2, bookmarks.size)
        // Verify newest first
        assertEquals(262, bookmarks[0].globalAyahNumber)
        assertEquals(1, bookmarks[1].globalAyahNumber)

        // Remove bookmarkNewer (toggle off)
        assertFalse(repository.toggleBookmark(bookmarkNewer))
        val bookmarksAfterRemoval = repository.getBookmarks()
        assertEquals(1, bookmarksAfterRemoval.size)
        assertEquals(1, bookmarksAfterRemoval[0].globalAyahNumber)
    }
}
