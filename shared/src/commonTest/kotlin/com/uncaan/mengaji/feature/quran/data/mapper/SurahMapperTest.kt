package com.uncaan.mengaji.feature.quran.data.mapper

import com.uncaan.mengaji.feature.quran.data.model.AyahDto
import com.uncaan.mengaji.feature.quran.data.model.EditionDto
import com.uncaan.mengaji.feature.quran.data.model.MushafPageEditionDto
import com.uncaan.mengaji.feature.quran.data.model.SurahDto
import com.uncaan.mengaji.feature.quran.data.model.SurahEditionDto
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SurahMapperTest {

    @Test
    fun surahSummaryDto_toDomain_mapsAllFieldsAndRevelationType() {
        val meccanDto = SurahSummaryDto(
            number = 1,
            name = "سُورَةُ ٱلْفَاتِحَةِ",
            englishName = "Al-Faatiha",
            englishNameTranslation = "The Opening",
            numberOfAyahs = 7,
            revelationType = "Meccan"
        )
        val medinanDto = SurahSummaryDto(
            number = 2,
            name = "سُورَةُ البَقَرَةِ",
            englishName = "Al-Baqara",
            englishNameTranslation = "The Cow",
            numberOfAyahs = 286,
            revelationType = "medinan"
        )

        val meccanDomain = meccanDto.toDomain()
        val medinanDomain = medinanDto.toDomain()

        assertEquals(1, meccanDomain.number)
        assertEquals("سُورَةُ ٱلْفَاتِحَةِ", meccanDomain.name)
        assertEquals("Al-Faatiha", meccanDomain.englishName)
        assertEquals("The Opening", meccanDomain.englishNameTranslation)
        assertEquals(7, meccanDomain.numberOfAyahs)
        assertEquals(RevelationType.MECCAN, meccanDomain.revelationType)

        assertEquals(2, medinanDomain.number)
        assertEquals(RevelationType.MEDINAN, medinanDomain.revelationType)
    }

    @Test
    fun surahEditionDtoList_toDomain_mergesMultiEditionAyahsAndInheritsParentSurahMetadata() {
        val arabicEdition = SurahEditionDto(
            number = 1,
            name = "سُورَةُ ٱلْفَاتِحَةِ",
            englishName = "Al-Faatiha",
            englishNameTranslation = "The Opening",
            revelationType = "Meccan",
            numberOfAyahs = 2,
            ayahs = listOf(
                AyahDto(
                    number = 1,
                    text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                    surah = null, // Omitted in /v1/surah/{id}/editions/... responses
                    numberInSurah = 1,
                    juz = 1,
                    page = 1,
                    hizbQuarter = 1
                ),
                AyahDto(
                    number = 2,
                    text = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ",
                    surah = null,
                    numberInSurah = 2,
                    juz = 1,
                    page = 1,
                    hizbQuarter = 1
                )
            ),
            edition = EditionDto(
                identifier = "quran-uthmani",
                language = "ar",
                format = "text",
                type = "quran"
            )
        )

        val translationEdition = SurahEditionDto(
            number = 1,
            name = "سُورَةُ ٱلْفَاتِحَةِ",
            englishName = "Al-Faatiha",
            englishNameTranslation = "The Opening",
            revelationType = "Meccan",
            numberOfAyahs = 2,
            ayahs = listOf(
                AyahDto(
                    number = 1,
                    text = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                    numberInSurah = 1,
                    juz = 1
                ),
                AyahDto(
                    number = 2,
                    text = "[All] praise is [due] to Allah, Lord of the worlds -",
                    numberInSurah = 2,
                    juz = 1
                )
            ),
            edition = EditionDto(
                identifier = "en.sahih",
                language = "en",
                format = "text",
                type = "translation"
            )
        )

        val audioEdition = SurahEditionDto(
            number = 1,
            name = "سُورَةُ ٱلْفَاتِحَةِ",
            englishName = "Al-Faatiha",
            englishNameTranslation = "The Opening",
            revelationType = "Meccan",
            numberOfAyahs = 2,
            ayahs = listOf(
                AyahDto(
                    number = 1,
                    text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                    audio = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
                    numberInSurah = 1,
                    juz = 1
                ),
                AyahDto(
                    number = 2,
                    text = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ",
                    audio = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/2.mp3",
                    numberInSurah = 2,
                    juz = 1
                )
            ),
            edition = EditionDto(
                identifier = "ar.alafasy",
                language = "ar",
                format = "audio",
                type = "versebyverse"
            )
        )

        val detail = listOf(arabicEdition, translationEdition, audioEdition).toDomain(
            translationEdition = "en.sahih",
            audioEdition = "ar.alafasy"
        )

        assertEquals(1, detail.number)
        assertEquals("Al-Faatiha", detail.englishName)
        assertEquals(RevelationType.MECCAN, detail.revelationType)
        assertEquals(2, detail.numberOfAyahs)
        assertEquals("en.sahih", detail.translationEdition)
        assertEquals("ar.alafasy", detail.audioEdition)
        assertEquals(2, detail.ayahs.size)

        val firstAyah = detail.ayahs[0]
        assertEquals(1, firstAyah.number)
        assertEquals("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", firstAyah.text)
        assertEquals("In the name of Allah, the Entirely Merciful, the Especially Merciful.", firstAyah.translation)
        assertEquals("https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3", firstAyah.audioUrl)
        assertEquals(1, firstAyah.surahNumber)
        assertEquals("Al-Faatiha", firstAyah.surahName)
        assertEquals("سُورَةُ ٱلْفَاتِحَةِ", firstAyah.surahArabicName)
        assertEquals("The Opening", firstAyah.englishNameTranslation)
        assertEquals(1, firstAyah.page)
        assertEquals(1, firstAyah.hizbQuarter)
    }

    @Test
    fun mushafPageEditionDtoList_toDomain_mergesAyahsAndExtractsSortedSurahs() {
        val surah112 = SurahSummaryDto(
            number = 112,
            name = "سُورَةُ الإِخْلَاصِ",
            englishName = "Al-Ikhlaas",
            englishNameTranslation = "Sincerity",
            numberOfAyahs = 4,
            revelationType = "Meccan"
        )
        val surah113 = SurahSummaryDto(
            number = 113,
            name = "سُورَةُ الفَلَقِ",
            englishName = "Al-Falaq",
            englishNameTranslation = "The Dawn",
            numberOfAyahs = 5,
            revelationType = "Meccan"
        )

        val arabicPage = MushafPageEditionDto(
            number = 604,
            ayahs = listOf(
                AyahDto(
                    number = 6222,
                    text = "قُلْ هُوَ ٱللَّهُ أَحَدٌ",
                    surah = SurahDto(
                        number = 112,
                        name = "سُورَةُ الإِخْلَاصِ",
                        englishName = "Al-Ikhlaas",
                        englishNameTranslation = "Sincerity",
                        numberOfAyahs = 4,
                        revelationType = "Meccan"
                    ),
                    numberInSurah = 1,
                    juz = 30,
                    page = 604,
                    hizbQuarter = 240
                )
            ),
            surahs = mapOf("113" to surah113, "112" to surah112),
            edition = EditionDto(
                identifier = "quran-uthmani",
                language = "ar",
                format = "text",
                type = "quran"
            )
        )

        val translationPage = MushafPageEditionDto(
            number = 604,
            ayahs = listOf(
                AyahDto(
                    number = 6222,
                    text = "Say, \"He is Allah, [who is] One,",
                    numberInSurah = 1,
                    juz = 30,
                    page = 604,
                    hizbQuarter = 240
                )
            ),
            surahs = mapOf("112" to surah112, "113" to surah113),
            edition = EditionDto(
                identifier = "en.sahih",
                language = "en",
                format = "text",
                type = "translation"
            )
        )

        val mushafPage = listOf(arabicPage, translationPage).toDomain(translationEdition = "en.sahih")

        assertEquals(604, mushafPage.pageNumber)
        assertEquals(30, mushafPage.juzNumber)
        assertEquals(240, mushafPage.hizbQuarter)
        assertEquals("en.sahih", mushafPage.translationEdition)
        assertEquals(2, mushafPage.surahs.size)
        assertEquals(112, mushafPage.surahs[0].number)
        assertEquals(113, mushafPage.surahs[1].number)
        assertEquals(1, mushafPage.ayahs.size)
        assertEquals("قُلْ هُوَ ٱللَّهُ أَحَدٌ", mushafPage.ayahs[0].text)
        assertEquals("Say, \"He is Allah, [who is] One,", mushafPage.ayahs[0].translation)
        assertEquals(604, mushafPage.ayahs[0].page)
    }

    @Test
    fun emptyEditionsList_toDomain_throwsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> {
            emptyList<SurahEditionDto>().toDomain()
        }
        assertFailsWith<IllegalArgumentException> {
            emptyList<MushafPageEditionDto>().toDomain()
        }
    }
}

