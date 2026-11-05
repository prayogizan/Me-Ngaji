package com.uncaan.mengaji.feature.quran.data.mapper

import com.uncaan.mengaji.feature.quran.data.model.AyahDto
import com.uncaan.mengaji.feature.quran.data.model.EditionDto
import com.uncaan.mengaji.feature.quran.data.model.SurahDto
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.Edition
import com.uncaan.mengaji.feature.quran.domain.model.Surah

/**
 * Maps a list of multi-edition [AyahDto] responses (Arabic text, translation, audio) to a single domain [Ayah] model.
 *
 * Extracts Arabic text from the Arabic edition DTO, translation text from the translation edition DTO,
 * and audio recitation URL from the audio edition DTO.
 *
 * @receiver List of [AyahDto] returned from a multi-edition API endpoint.
 * @return The unified domain [Ayah] model.
 * @throws IllegalArgumentException If the editions list is empty.
 * @see Ayah
 */
fun List<AyahDto>.toDomain(): Ayah {
    require(isNotEmpty()) { "Ayah editions list cannot be empty" }

    val arabicDto = find {
        val edition = it.edition
        edition?.identifier == "quran-uthmani" || (edition != null && edition.language == "ar" && edition.format == "text")
    } ?: first()
    val translationDto = find {
        val edition = it.edition
        edition?.type == "translation" || (edition != null && edition.format == "text" && it != arabicDto)
    }
    val audioDto = find {
        it.audio != null || it.edition?.format == "audio"
    }

    val base = arabicDto
    val surah = base.surah

    return Ayah(
        number = base.number,
        text = arabicDto.text,
        translation = translationDto?.text ?: "",
        audioUrl = audioDto?.audio ?: base.audio,
        surahNumber = surah?.number ?: 0,
        surahName = surah?.englishName ?: "",
        surahArabicName = surah?.name ?: "",
        englishNameTranslation = surah?.englishNameTranslation ?: "",
        numberInSurah = base.numberInSurah,
        juz = base.juz,
        page = base.page ?: 1,
        hizbQuarter = base.hizbQuarter ?: 1
    )
}

/**
 * Maps a single [AyahDto] to the domain [Ayah] model.
 *
 * @receiver The raw [AyahDto].
 * @param translationText Optional translated string to populate the [Ayah.translation] field.
 * @return The mapped domain [Ayah].
 */
fun AyahDto.toDomain(translationText: String = ""): Ayah {
    val surah = this.surah
    return Ayah(
        number = this.number,
        text = this.text,
        translation = translationText,
        audioUrl = this.audio,
        surahNumber = surah?.number ?: 0,
        surahName = surah?.englishName ?: "",
        surahArabicName = surah?.name ?: "",
        englishNameTranslation = surah?.englishNameTranslation ?: "",
        numberInSurah = this.numberInSurah,
        juz = this.juz,
        page = this.page ?: 1,
        hizbQuarter = this.hizbQuarter ?: 1
    )
}

/**
 * Maps a [SurahDto] from the data layer to the domain [Surah] entity.
 *
 * @receiver The raw [SurahDto].
 * @return The mapped domain [Surah].
 */
fun SurahDto.toDomain(): Surah {
    return Surah(
        number = this.number,
        name = this.name,
        englishName = this.englishName,
        englishNameTranslation = this.englishNameTranslation ?: "",
        numberOfAyahs = this.numberOfAyahs ?: 0,
        revelationType = this.revelationType ?: ""
    )
}

/**
 * Maps an [EditionDto] from the data layer to the domain [Edition] entity.
 *
 * @receiver The raw [EditionDto].
 * @return The mapped domain [Edition].
 */
fun EditionDto.toDomain(): Edition {
    return Edition(
        identifier = this.identifier,
        language = this.language,
        name = this.name,
        englishName = this.englishName,
        format = this.format,
        type = this.type
    )
}
