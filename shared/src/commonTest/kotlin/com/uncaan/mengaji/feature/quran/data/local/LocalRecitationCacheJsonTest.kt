package com.uncaan.mengaji.feature.quran.data.local

import com.uncaan.mengaji.feature.quran.data.model.AyahBookmarkDto
import com.uncaan.mengaji.feature.quran.data.model.LastReadProgressDto
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LocalRecitationCacheJsonTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Test
    fun lastReadProgressDto_serializesAndDeserializesCorrectly() {
        val dto = LastReadProgressDto(
            surahNumber = 2,
            surahNameEnglish = "Al-Baqara",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 255,
            pageNumber = 42,
            juzNumber = 3,
            preferredReaderMode = "VERSE_LIST",
            updatedAtEpochMillis = 1730000000000L
        )

        val serialized = json.encodeToString(dto)
        val deserialized = json.decodeFromString<LastReadProgressDto>(serialized)

        assertEquals(dto, deserialized)
    }

    @Test
    fun lastReadProgressDto_malformedJsonGracefullyRecoversToNull() {
        val malformedJson = "{ \"surahNumber\": \"not_a_number\", broken json"

        val result = try {
            json.decodeFromString<LastReadProgressDto>(malformedJson)
        } catch (_: Throwable) {
            null
        }

        assertNull(result)
    }

    @Test
    fun ayahBookmarkDtoList_serializesAndDeserializesCorrectly() {
        val bookmarks = listOf(
            AyahBookmarkDto(
                globalAyahNumber = 262,
                surahNumber = 2,
                surahNameEnglish = "Al-Baqara",
                surahNameArabic = "سُورَةُ البَقَرَةِ",
                ayahNumberInSurah = 255,
                pageNumber = 42,
                arabicSnippet = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
                savedAtEpochMillis = 1730000002000L
            ),
            AyahBookmarkDto(
                globalAyahNumber = 1,
                surahNumber = 1,
                surahNameEnglish = "Al-Faatiha",
                surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
                ayahNumberInSurah = 1,
                pageNumber = 1,
                arabicSnippet = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                savedAtEpochMillis = 1730000001000L
            )
        )

        val serialized = json.encodeToString(bookmarks)
        val deserialized = json.decodeFromString<List<AyahBookmarkDto>>(serialized)

        assertEquals(2, deserialized.size)
        assertEquals(262, deserialized[0].globalAyahNumber)
        assertEquals(1, deserialized[1].globalAyahNumber)
    }

    @Test
    fun ayahBookmarkDtoList_malformedJsonGracefullyRecoversToEmptyList() {
        val malformedJson = "[ { broken_json "

        val result = try {
            json.decodeFromString<List<AyahBookmarkDto>>(malformedJson)
        } catch (_: Throwable) {
            emptyList()
        }

        assertTrue(result.isEmpty())
    }

    @Test
    fun surahCatalogCache_serializesAndDeserializesCorrectly() {
        val catalog = listOf(
            SurahSummaryDto(
                number = 1,
                name = "سُورَةُ ٱلْفَاتِحَةِ",
                englishName = "Al-Faatiha",
                englishNameTranslation = "The Opening",
                numberOfAyahs = 7,
                revelationType = "Meccan"
            )
        )

        val serialized = json.encodeToString(catalog)
        val deserialized = json.decodeFromString<List<SurahSummaryDto>>(serialized)

        assertNotNull(deserialized)
        assertEquals(1, deserialized.size)
        assertEquals("Al-Faatiha", deserialized[0].englishName)
    }

    @Test
    fun surahCatalogCache_malformedJsonGracefullyRecoversToNull() {
        val malformedJson = "{ invalid"

        val result = try {
            json.decodeFromString<List<SurahSummaryDto>>(malformedJson)
        } catch (_: Throwable) {
            null
        }

        assertNull(result)
    }
}

