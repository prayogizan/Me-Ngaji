package com.uncaan.mengaji.feature.quran.data.mapper

import com.uncaan.mengaji.feature.quran.data.model.AyahBookmarkDto
import com.uncaan.mengaji.feature.quran.data.model.LastReadProgressDto
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import kotlin.test.Test
import kotlin.test.assertEquals

class RecitationMapperTest {

    @Test
    fun lastReadProgressDto_toDomain_mapsAllFieldsCorrectly() {
        val dto = LastReadProgressDto(
            surahNumber = 36,
            surahNameEnglish = "Yaseen",
            surahNameArabic = "سُورَةُ يس",
            ayahNumberInSurah = 1,
            pageNumber = 440,
            juzNumber = 22,
            preferredReaderMode = "MUSHAF_PAGE",
            updatedAtEpochMillis = 1730005000000L
        )

        val domain = dto.toDomain()

        assertEquals(36, domain.surahNumber)
        assertEquals("Yaseen", domain.surahNameEnglish)
        assertEquals("سُورَةُ يس", domain.surahNameArabic)
        assertEquals(1, domain.ayahNumberInSurah)
        assertEquals(440, domain.pageNumber)
        assertEquals(22, domain.juzNumber)
        assertEquals(ReaderMode.MUSHAF_PAGE, domain.preferredReaderMode)
        assertEquals(1730005000000L, domain.updatedAtEpochMillis)
    }

    @Test
    fun lastReadProgressDto_unknownReaderMode_defaultsToVerseList() {
        val dto = LastReadProgressDto(
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            juzNumber = 1,
            preferredReaderMode = "UNKNOWN_OR_INVALID_MODE",
            updatedAtEpochMillis = 1730005000000L
        )

        val domain = dto.toDomain()

        assertEquals(ReaderMode.VERSE_LIST, domain.preferredReaderMode)
    }

    @Test
    fun lastReadProgress_toDto_mapsAllFieldsCorrectly() {
        val domain = LastReadProgress(
            surahNumber = 18,
            surahNameEnglish = "Al-Kahf",
            surahNameArabic = "سُورَةُ الكَهْفِ",
            ayahNumberInSurah = 10,
            pageNumber = 294,
            juzNumber = 15,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1730006000000L
        )

        val dto = domain.toDto()

        assertEquals(18, dto.surahNumber)
        assertEquals("Al-Kahf", dto.surahNameEnglish)
        assertEquals("سُورَةُ الكَهْفِ", dto.surahNameArabic)
        assertEquals(10, dto.ayahNumberInSurah)
        assertEquals(294, dto.pageNumber)
        assertEquals(15, dto.juzNumber)
        assertEquals("VERSE_LIST", dto.preferredReaderMode)
        assertEquals(1730006000000L, dto.updatedAtEpochMillis)
    }

    @Test
    fun ayahBookmark_toDomainAndToDto_preservesIntegrity() {
        val bookmark = AyahBookmark(
            globalAyahNumber = 293,
            surahNumber = 2,
            surahNameEnglish = "Al-Baqara",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 286,
            pageNumber = 49,
            arabicSnippet = "لَا يُكَلِّفُ ٱللَّهُ نَفْسًا إِلَّا وُسْعَهَا",
            savedAtEpochMillis = 1730007000000L
        )

        val dto = bookmark.toDto()
        val mappedBack = dto.toDomain()

        assertEquals(bookmark, mappedBack)
    }

    @Test
    fun surahSummary_toDto_preservesProperties() {
        val summary = SurahSummary(
            number = 114,
            name = "سُورَةُ النَّاسِ",
            englishName = "An-Naas",
            englishNameTranslation = "Mankind",
            numberOfAyahs = 6,
            revelationType = RevelationType.MECCAN
        )

        val dto = summary.toDto()

        assertEquals(114, dto.number)
        assertEquals("An-Naas", dto.englishName)
        assertEquals("Meccan", dto.revelationType)
    }
}

