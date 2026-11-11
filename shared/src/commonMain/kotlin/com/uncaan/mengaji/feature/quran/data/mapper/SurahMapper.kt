package com.uncaan.mengaji.feature.quran.data.mapper

import com.uncaan.mengaji.feature.quran.data.model.MushafPageEditionDto
import com.uncaan.mengaji.feature.quran.data.model.SurahDto
import com.uncaan.mengaji.feature.quran.data.model.SurahEditionDto
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import kotlin.jvm.JvmName

/**
 * Maps a [SurahSummaryDto] from the data layer to a pure domain [SurahSummary] entity.
 *
 * Converts the raw string [SurahSummaryDto.revelationType] into a strongly-typed
 * [RevelationType] enum value.
 *
 * @receiver The raw [SurahSummaryDto] received from the network or cache layer.
 * @return A mapped [SurahSummary] domain model.
 * @see SurahSummary
 * @see RevelationType
 */
fun SurahSummaryDto.toDomain(): SurahSummary {
    return SurahSummary(
        number = this.number,
        name = this.name,
        englishName = this.englishName,
        englishNameTranslation = this.englishNameTranslation,
        numberOfAyahs = this.numberOfAyahs,
        revelationType = RevelationType.fromApiValue(this.revelationType)
    )
}

/**
 * Maps a [SurahDto] to a domain [SurahSummary] entity.
 *
 * Used as a fallback when reconstructing page-level Surah summaries from embedded verse metadata.
 *
 * @receiver The raw [SurahDto] embedded in an [com.uncaan.mengaji.feature.quran.data.model.AyahDto].
 * @return A mapped [SurahSummary] domain model.
 */
fun SurahDto.toSurahSummary(): SurahSummary {
    return SurahSummary(
        number = this.number,
        name = this.name,
        englishName = this.englishName,
        englishNameTranslation = this.englishNameTranslation ?: "",
        numberOfAyahs = this.numberOfAyahs ?: 0,
        revelationType = RevelationType.fromApiValue(this.revelationType)
    )
}

/**
 * Merges a multi-edition list of [SurahEditionDto] payloads into a unified domain [SurahDetail] entity.
 *
 * Pairs Uthmani Arabic (`quran-uthmani`), the requested [translationEdition], and the requested
 * [audioEdition] in $O(N)$ time by indexing verses by `numberInSurah` and global `number`.
 * When individual verse items omit a nested `surah` object (as returned by `/v1/surah/{id}/editions/...`),
 * parent chapter metadata from the enclosing [SurahEditionDto] is inherited automatically.
 *
 * @receiver List of [SurahEditionDto] objects returned by the multi-edition Surah endpoint.
 * @param translationEdition Expected translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
 * @param audioEdition Expected audio recitation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_RECITATION].
 * @return A consolidated [SurahDetail] domain entity with merged verse text, translation, and audio URLs.
 * @throws IllegalArgumentException If the receiver edition list is empty.
 * @see SurahDetail
 * @see SurahEditionDto
 */
@JvmName("surahEditionsToDomain")
fun List<SurahEditionDto>.toDomain(
    translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
    audioEdition: String = QuranEditionPresets.DEFAULT_RECITATION
): SurahDetail {
    require(isNotEmpty()) { "Surah editions list cannot be empty" }

    val arabicEdition = find {
        val ed = it.edition
        ed?.identifier == "quran-uthmani" || (ed != null && ed.language == "ar" && ed.format == "text")
    } ?: first()

    val translationEditionDto = find {
        val ed = it.edition
        ed?.identifier == translationEdition ||
            ed?.type == "translation" ||
            (ed != null && ed.format == "text" && it != arabicEdition)
    }

    val audioEditionDto = find {
        val ed = it.edition
        ed?.identifier == audioEdition ||
            ed?.format == "audio" ||
            it.ayahs.any { ayah -> !ayah.audio.isNullOrBlank() }
    }

    val translationByNumberInSurah = translationEditionDto?.ayahs?.associateBy { it.numberInSurah }.orEmpty()
    val translationByGlobalNumber = translationEditionDto?.ayahs?.associateBy { it.number }.orEmpty()
    val audioByNumberInSurah = audioEditionDto?.ayahs?.associateBy { it.numberInSurah }.orEmpty()
    val audioByGlobalNumber = audioEditionDto?.ayahs?.associateBy { it.number }.orEmpty()

    val mergedAyahs = arabicEdition.ayahs.map { arabicAyah ->
        val translationAyah = translationByNumberInSurah[arabicAyah.numberInSurah]
            ?: translationByGlobalNumber[arabicAyah.number]
        val audioAyah = audioByNumberInSurah[arabicAyah.numberInSurah]
            ?: audioByGlobalNumber[arabicAyah.number]
        val surahMeta = arabicAyah.surah

        Ayah(
            number = arabicAyah.number,
            text = arabicAyah.text,
            translation = translationAyah?.text ?: "",
            audioUrl = audioAyah?.audio ?: arabicAyah.audio,
            surahNumber = surahMeta?.number ?: arabicEdition.number,
            surahName = surahMeta?.englishName ?: arabicEdition.englishName,
            surahArabicName = surahMeta?.name ?: arabicEdition.name,
            englishNameTranslation = surahMeta?.englishNameTranslation ?: arabicEdition.englishNameTranslation,
            numberInSurah = arabicAyah.numberInSurah,
            juz = arabicAyah.juz,
            page = arabicAyah.page ?: 1,
            hizbQuarter = arabicAyah.hizbQuarter ?: 1
        )
    }

    val resolvedNumberOfAyahs = if (arabicEdition.numberOfAyahs > 0) {
        arabicEdition.numberOfAyahs
    } else {
        mergedAyahs.size
    }

    return SurahDetail(
        number = arabicEdition.number,
        name = arabicEdition.name,
        englishName = arabicEdition.englishName,
        englishNameTranslation = arabicEdition.englishNameTranslation,
        revelationType = RevelationType.fromApiValue(arabicEdition.revelationType),
        numberOfAyahs = resolvedNumberOfAyahs,
        ayahs = mergedAyahs,
        translationEdition = translationEditionDto?.edition?.identifier?.ifBlank { null } ?: translationEdition,
        audioEdition = audioEditionDto?.edition?.identifier?.ifBlank { null } ?: audioEdition
    )
}

/**
 * Merges a multi-edition list of [MushafPageEditionDto] payloads into a unified domain [MushafPage] entity.
 *
 * Pairs Uthmani Arabic (`quran-uthmani`) and the requested [translationEdition] in $O(N)$ time
 * by indexing verses by global `number`. Extracts all Surahs appearing on the page sorted by
 * Surah number, and derives page-level `juzNumber` and `hizbQuarter` from the first verse on the page.
 *
 * @receiver List of [MushafPageEditionDto] objects returned by the multi-edition Mushaf page endpoint.
 * @param translationEdition Expected translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
 * @return A consolidated [MushafPage] domain entity.
 * @throws IllegalArgumentException If the receiver edition list is empty.
 * @see MushafPage
 * @see MushafPageEditionDto
 */
@JvmName("mushafPageEditionsToDomain")
fun List<MushafPageEditionDto>.toDomain(
    translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
    audioEdition: String = QuranEditionPresets.DEFAULT_RECITATION
): MushafPage {
    require(isNotEmpty()) { "Mushaf page editions list cannot be empty" }

    val arabicEdition = find {
        val ed = it.edition
        ed?.identifier == "quran-uthmani" || (ed != null && ed.language == "ar" && ed.format == "text")
    } ?: first()

    val translationEditionDto = find {
        val ed = it.edition
        ed?.identifier == translationEdition ||
            ed?.type == "translation" ||
            (ed != null && ed.format == "text" && it != arabicEdition)
    }

    val translationByGlobalNumber = translationEditionDto?.ayahs?.associateBy { it.number }.orEmpty()
    val surahsByNumber = arabicEdition.surahs.values.associateBy { it.number }

    val mergedAyahs = arabicEdition.ayahs.map { arabicAyah ->
        val translationAyah = translationByGlobalNumber[arabicAyah.number]
        val surahMeta = arabicAyah.surah
        val fallbackSurahSummary = surahMeta?.number?.let { surahsByNumber[it] }
        val resolvedAudioUrl = arabicAyah.audio?.takeIf { it.isNotBlank() }
            ?: "https://cdn.islamic.network/quran/audio/128/$audioEdition/${arabicAyah.number}.mp3"

        Ayah(
            number = arabicAyah.number,
            text = arabicAyah.text.removePrefix("\uFEFF"),
            translation = translationAyah?.text ?: "",
            audioUrl = resolvedAudioUrl,
            surahNumber = surahMeta?.number ?: fallbackSurahSummary?.number ?: 0,
            surahName = surahMeta?.englishName ?: fallbackSurahSummary?.englishName ?: "",
            surahArabicName = surahMeta?.name ?: fallbackSurahSummary?.name ?: "",
            englishNameTranslation = surahMeta?.englishNameTranslation
                ?: fallbackSurahSummary?.englishNameTranslation
                ?: "",
            numberInSurah = arabicAyah.numberInSurah,
            juz = arabicAyah.juz,
            page = arabicAyah.page ?: arabicEdition.number,
            hizbQuarter = arabicAyah.hizbQuarter ?: 1
        )
    }

    val pageSurahs = if (arabicEdition.surahs.isNotEmpty()) {
        arabicEdition.surahs.values
            .map { it.toDomain() }
            .sortedBy { it.number }
    } else {
        arabicEdition.ayahs
            .mapNotNull { it.surah?.toSurahSummary() }
            .distinctBy { it.number }
            .sortedBy { it.number }
    }

    val firstAyah = mergedAyahs.firstOrNull()

    return MushafPage(
        pageNumber = arabicEdition.number,
        ayahs = mergedAyahs,
        surahs = pageSurahs,
        juzNumber = firstAyah?.juz ?: 1,
        hizbQuarter = firstAyah?.hizbQuarter ?: 1,
        translationEdition = translationEditionDto?.edition?.identifier?.ifBlank { null } ?: translationEdition
    )
}

