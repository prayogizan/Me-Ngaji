package com.uncaan.mengaji.feature.widget.data.mapper

import com.uncaan.mengaji.feature.widget.data.model.DailyAyahEditionDto
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah

/**
 * Transforms a multi-edition list of [DailyAyahEditionDto] received from the network into a single [DailyAyah] domain entity.
 *
 * The Al-Quran Cloud API returns an array containing two edition objects for the requested verse:
 * - One representing the Arabic text (`edition.identifier == "quran-uthmani"`)
 * - One representing the Indonesian translation (`edition.identifier == "id.indonesian"`)
 *
 * This mapper pairs the two editions, extracts the appropriate fields, and stamps the verse with the [fetchedDate].
 *
 * @receiver The raw list of edition DTOs from the API response envelope.
 * @param fetchedDate The ISO-8601 formatted calendar date ("yyyy-MM-dd") when the quote was fetched.
 * @param expectedEdition Identifier of the expected translation edition (e.g., "id.indonesian", "en.asad").
 *                        Defaults to `"id.indonesian"`.
 * @return A consolidated [DailyAyah] domain model.
 * @throws IllegalStateException If either the Arabic edition or translation edition is missing from the list.
 * @see DailyAyahEditionDto
 * @see DailyAyah
 */
fun List<DailyAyahEditionDto>.toDailyAyah(
    fetchedDate: String,
    expectedEdition: String = "id.indonesian"
): DailyAyah {
    val arabicEdition = find { it.edition.identifier == "quran-uthmani" }
        ?: find { it.edition.type == "quran" }
        ?: throw IllegalStateException("Multi-edition response missing Arabic 'quran-uthmani' edition")

    val translationEdition = find { it.edition.identifier == expectedEdition }
        ?: find { it.edition.type == "translation" }
        ?: throw IllegalStateException("Multi-edition response missing translation edition for '$expectedEdition'")

    return DailyAyah(
        arabicText = arabicEdition.text,
        translationText = translationEdition.text,
        surahName = arabicEdition.surah.englishName,
        surahNameArabic = arabicEdition.surah.name,
        surahNumber = arabicEdition.surah.number,
        ayahNumberInSurah = arabicEdition.numberInSurah,
        globalAyahNumber = arabicEdition.number,
        translationEdition = translationEdition.edition.identifier,
        fetchedDate = fetchedDate
    )
}
