package com.uncaan.mengaji.feature.widget.data.mapper

import com.uncaan.mengaji.feature.widget.data.model.DailyAyahEditionDetailDto
import com.uncaan.mengaji.feature.widget.data.model.DailyAyahEditionDto
import com.uncaan.mengaji.feature.widget.data.model.DailyAyahSurahDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DailyAyahMapperTest {

    private val sampleSurah = DailyAyahSurahDto(
        number = 34,
        name = "سُورَةُ سَبَإٍ",
        englishName = "Saba"
    )

    private val sampleArabicEdition = DailyAyahEditionDto(
        number = 3644,
        text = "وَٱلَّذِينَ يَسْعَوْنَ فِىٓ ءَايَٰتِنَا مُعَٰجِزِينَ أُو۟لَٰٓئِكَ فِى ٱلْعَذَابِ مُحْضَرُونَ",
        edition = DailyAyahEditionDetailDto(
            identifier = "quran-uthmani",
            language = "ar",
            type = "quran"
        ),
        surah = sampleSurah,
        numberInSurah = 38,
        juz = 22
    )

    private val sampleIndonesianEdition = DailyAyahEditionDto(
        number = 3644,
        text = "Dan orang-orang yang berusaha menentang ayat-ayat Kami...",
        edition = DailyAyahEditionDetailDto(
            identifier = "id.indonesian",
            language = "id",
            type = "translation"
        ),
        surah = sampleSurah,
        numberInSurah = 38,
        juz = 22
    )

    private val sampleEnglishEdition = DailyAyahEditionDto(
        number = 3644,
        text = "Whereas those who strive against Our messages...",
        edition = DailyAyahEditionDetailDto(
            identifier = "en.asad",
            language = "en",
            type = "translation"
        ),
        surah = sampleSurah,
        numberInSurah = 38,
        juz = 22
    )

    @Test
    fun toDailyAyah_englishEdition_returnsMappedEnglishDomainEntity() {
        val list = listOf(sampleArabicEdition, sampleEnglishEdition)
        val today = "2026-10-30"

        val domain = list.toDailyAyah(fetchedDate = today, expectedEdition = "en.asad")

        assertEquals("Whereas those who strive against Our messages...", domain.translationText)
        assertEquals("en.asad", domain.translationEdition)
        assertEquals("Saba", domain.surahName)
    }

    @Test
    fun toDailyAyah_validMultiEditionList_returnsMappedDomainEntity() {
        val list = listOf(sampleArabicEdition, sampleIndonesianEdition)
        val today = "2026-10-22"

        val domain = list.toDailyAyah(fetchedDate = today)

        assertEquals("وَٱلَّذِينَ يَسْعَوْنَ فِىٓ ءَايَٰتِنَا مُعَٰجِزِينَ أُو۟لَٰٓئِكَ فِى ٱلْعَذَابِ مُحْضَرُونَ", domain.arabicText)
        assertEquals("Dan orang-orang yang berusaha menentang ayat-ayat Kami...", domain.translationText)
        assertEquals("Saba", domain.surahName)
        assertEquals("سُورَةُ سَبَإٍ", domain.surahNameArabic)
        assertEquals(34, domain.surahNumber)
        assertEquals(38, domain.ayahNumberInSurah)
        assertEquals(3644, domain.globalAyahNumber)
        assertEquals("id.indonesian", domain.translationEdition)
        assertEquals("2026-10-22", domain.fetchedDate)
    }

    @Test
    fun toDailyAyah_orderIndependent_pairsCorrectlyWhenTranslationFirst() {
        val reversedList = listOf(sampleIndonesianEdition, sampleArabicEdition)
        val domain = reversedList.toDailyAyah("2026-10-22")

        assertEquals(sampleArabicEdition.text, domain.arabicText)
        assertEquals(sampleIndonesianEdition.text, domain.translationText)
    }

    @Test
    fun toDailyAyah_missingArabicEdition_throwsIllegalStateException() {
        val listWithoutArabic = listOf(sampleIndonesianEdition)

        assertFailsWith<IllegalStateException> {
            listWithoutArabic.toDailyAyah("2026-10-22")
        }
    }

    @Test
    fun toDailyAyah_missingIndonesianTranslation_throwsIllegalStateException() {
        val listWithoutTranslation = listOf(sampleArabicEdition)

        assertFailsWith<IllegalStateException> {
            listWithoutTranslation.toDailyAyah("2026-10-22")
        }
    }

    @Test
    fun toDailyAyah_emptyList_throwsIllegalStateException() {
        assertFailsWith<IllegalStateException> {
            emptyList<DailyAyahEditionDto>().toDailyAyah("2026-10-22")
        }
    }
}
