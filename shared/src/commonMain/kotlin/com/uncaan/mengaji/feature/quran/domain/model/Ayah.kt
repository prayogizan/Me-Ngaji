package com.uncaan.mengaji.feature.quran.domain.model

data class Ayah(
    val number: Int,
    val text: String,
    val translation: String,
    val audioUrl: String?,
    val surahNumber: Int,
    val surahName: String,
    val surahArabicName: String = "",
    val englishNameTranslation: String = "",
    val numberInSurah: Int,
    val juz: Int
)
