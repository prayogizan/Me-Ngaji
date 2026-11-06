package com.uncaan.mengaji.feature.quran.data.mapper

import com.uncaan.mengaji.feature.quran.data.model.AyahBookmarkDto
import com.uncaan.mengaji.feature.quran.data.model.LastReadProgressDto
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary

/**
 * Maps a [LastReadProgressDto] to the domain model [LastReadProgress].
 *
 * Gracefully defaults unrecognized [LastReadProgressDto.preferredReaderMode] strings to [ReaderMode.VERSE_LIST].
 *
 * @receiver The raw [LastReadProgressDto] stored in local cache.
 * @return The mapped pure domain entity [LastReadProgress].
 * @see LastReadProgress
 */
fun LastReadProgressDto.toDomain(): LastReadProgress {
    val resolvedMode = try {
        ReaderMode.valueOf(this.preferredReaderMode)
    } catch (_: Exception) {
        ReaderMode.VERSE_LIST
    }

    return LastReadProgress(
        surahNumber = this.surahNumber,
        surahNameEnglish = this.surahNameEnglish,
        surahNameArabic = this.surahNameArabic,
        ayahNumberInSurah = this.ayahNumberInSurah,
        pageNumber = this.pageNumber,
        juzNumber = this.juzNumber,
        preferredReaderMode = resolvedMode,
        updatedAtEpochMillis = this.updatedAtEpochMillis
    )
}

/**
 * Maps a domain [LastReadProgress] entity to the serializable [LastReadProgressDto].
 *
 * @receiver The domain [LastReadProgress] to serialize.
 * @return The mapped [LastReadProgressDto] ready for JSON persistence.
 * @see LastReadProgressDto
 */
fun LastReadProgress.toDto(): LastReadProgressDto {
    return LastReadProgressDto(
        surahNumber = this.surahNumber,
        surahNameEnglish = this.surahNameEnglish,
        surahNameArabic = this.surahNameArabic,
        ayahNumberInSurah = this.ayahNumberInSurah,
        pageNumber = this.pageNumber,
        juzNumber = this.juzNumber,
        preferredReaderMode = this.preferredReaderMode.name,
        updatedAtEpochMillis = this.updatedAtEpochMillis
    )
}

/**
 * Maps an [AyahBookmarkDto] to the domain entity [AyahBookmark].
 *
 * @receiver The raw [AyahBookmarkDto] stored in local preferences.
 * @return The mapped domain entity [AyahBookmark].
 * @see AyahBookmark
 */
fun AyahBookmarkDto.toDomain(): AyahBookmark {
    return AyahBookmark(
        globalAyahNumber = this.globalAyahNumber,
        surahNumber = this.surahNumber,
        surahNameEnglish = this.surahNameEnglish,
        surahNameArabic = this.surahNameArabic,
        ayahNumberInSurah = this.ayahNumberInSurah,
        pageNumber = this.pageNumber,
        arabicSnippet = this.arabicSnippet,
        savedAtEpochMillis = this.savedAtEpochMillis
    )
}

/**
 * Maps a domain [AyahBookmark] entity to the serializable [AyahBookmarkDto].
 *
 * @receiver The domain [AyahBookmark] to serialize.
 * @return The mapped [AyahBookmarkDto] ready for JSON persistence.
 * @see AyahBookmarkDto
 */
fun AyahBookmark.toDto(): AyahBookmarkDto {
    return AyahBookmarkDto(
        globalAyahNumber = this.globalAyahNumber,
        surahNumber = this.surahNumber,
        surahNameEnglish = this.surahNameEnglish,
        surahNameArabic = this.surahNameArabic,
        ayahNumberInSurah = this.ayahNumberInSurah,
        pageNumber = this.pageNumber,
        arabicSnippet = this.arabicSnippet,
        savedAtEpochMillis = this.savedAtEpochMillis
    )
}

/**
 * Maps a domain [SurahSummary] entity to the serializable [SurahSummaryDto].
 *
 * Enables caching domain catalog entries back into the local offline catalog cache.
 *
 * @receiver The domain [SurahSummary] entity.
 * @return The mapped [SurahSummaryDto].
 * @see SurahSummaryDto
 */
fun SurahSummary.toDto(): SurahSummaryDto {
    return SurahSummaryDto(
        number = this.number,
        name = this.name,
        englishName = this.englishName,
        englishNameTranslation = this.englishNameTranslation,
        numberOfAyahs = this.numberOfAyahs,
        revelationType = this.revelationType.displayName
    )
}

