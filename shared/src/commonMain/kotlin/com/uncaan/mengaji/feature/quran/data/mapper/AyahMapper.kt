package com.uncaan.mengaji.feature.quran.data.mapper

import com.uncaan.mengaji.feature.quran.data.model.AyahDto
import com.uncaan.mengaji.feature.quran.data.model.EditionDto
import com.uncaan.mengaji.feature.quran.data.model.SurahDto
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.Edition
import com.uncaan.mengaji.feature.quran.domain.model.Surah

fun AyahDto.toDomain(translationText: String = ""): Ayah {
    return Ayah(
        number = this.number,
        text = this.text,
        translation = translationText,
        audioUrl = this.audio,
        surahNumber = this.surah.number,
        surahName = this.surah.englishName,
        numberInSurah = this.numberInSurah,
        juz = this.juz
    )
}

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
