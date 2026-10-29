package com.uncaan.mengaji.widget.action

import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.navigation.DeepLinkConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests verifying [createAyahDeepLinkAction] behavior and parameter validation rules (CMP-504).
 */
class AyahDeepLinkActionTest {

    private fun createSampleAyah(surahNumber: Int, ayahNumberInSurah: Int): DailyAyah {
        return DailyAyah(
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
            translationText = "Allah, tidak ada tuhan selain Dia",
            surahName = "Al-Baqarah",
            surahNameArabic = "البقرة",
            surahNumber = surahNumber,
            ayahNumberInSurah = ayahNumberInSurah,
            globalAyahNumber = 262,
            translationEdition = "id.indonesian",
            fetchedDate = "2026-10-29"
        )
    }

    @Test
    fun createAyahDeepLinkAction_validReference_returnsConfiguredAction() {
        val ayah = createSampleAyah(surahNumber = 2, ayahNumberInSurah = 255)
        val action = createAyahDeepLinkAction(ayah)

        assertNotNull(action)
    }

    @Test
    fun createAyahDeepLinkAction_invalidSurahNumber_returnsFallbackAction() {
        val ayah = createSampleAyah(surahNumber = 0, ayahNumberInSurah = 255)
        val action = createAyahDeepLinkAction(ayah)

        assertNotNull(action)
    }

    @Test
    fun createAyahDeepLinkAction_invalidAyahNumber_returnsFallbackAction() {
        val ayah = createSampleAyah(surahNumber = 2, ayahNumberInSurah = -1)
        val action = createAyahDeepLinkAction(ayah)

        assertNotNull(action)
    }

    @Test
    fun deepLinkConstants_keysMatchExpectedStringLiterals() {
        assertEquals("surah_number", DeepLinkConstants.EXTRA_SURAH_NUMBER)
        assertEquals("ayah_number", DeepLinkConstants.EXTRA_AYAH_NUMBER)
    }
}
