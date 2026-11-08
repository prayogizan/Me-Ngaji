package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Domain entity representing a single Juz (part) summary in the Quran catalog.
 *
 * Each Juz spans specific Surah and Ayah boundaries as well as Medina Mushaf page ranges.
 *
 * @property number The 1-indexed Juz ordinal number (`1..30`).
 * @property nameArabic The traditional Arabic title of the Juz (e.g., `"الجزء ١"` or `"آلم"`).
 * @property startSurahNumber The 1-indexed Surah number where this Juz begins.
 * @property startSurahNameEnglish The transliterated English name of the starting Surah.
 * @property startSurahNameArabic The Arabic script name of the starting Surah.
 * @property startAyahNumberInSurah The Ayah number within the starting Surah where this Juz begins.
 * @property startPageNumber The starting Medina Mushaf page number (`1..604`).
 * @property endSurahNumber The 1-indexed Surah number where this Juz ends.
 * @property endSurahNameEnglish The transliterated English name of the ending Surah.
 * @property endSurahNameArabic The Arabic script name of the ending Surah.
 * @property endAyahNumberInSurah The Ayah number within the ending Surah where this Juz ends.
 * @property endPageNumber The ending Medina Mushaf page number (`1..604`).
 * @property totalVerses The total number of verses (Ayahs) contained within this Juz.
 */
data class JuzSummary(
    val number: Int,
    val nameArabic: String,
    val startSurahNumber: Int,
    val startSurahNameEnglish: String,
    val startSurahNameArabic: String,
    val startAyahNumberInSurah: Int,
    val startPageNumber: Int,
    val endSurahNumber: Int,
    val endSurahNameEnglish: String,
    val endSurahNameArabic: String,
    val endAyahNumberInSurah: Int,
    val endPageNumber: Int,
    val totalVerses: Int
)

/**
 * Static catalog repository providing canonical mapping metadata for all 30 Quranic Juzs.
 *
 * Mappings correspond to standard 604-page Medina Mushaf conventions and authentic Ayah boundaries.
 */
object JuzCatalogData {

    /**
     * Complete immutable list of all 30 Quranic [JuzSummary] entries.
     */
    val allJuz: List<JuzSummary> = listOf(
        JuzSummary(
            number = 1,
            nameArabic = "الجزء ١",
            startSurahNumber = 1,
            startSurahNameEnglish = "Al-Fatihah",
            startSurahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            startAyahNumberInSurah = 1,
            startPageNumber = 1,
            endSurahNumber = 2,
            endSurahNameEnglish = "Al-Baqarah",
            endSurahNameArabic = "سُورَةُ البَقَرَةِ",
            endAyahNumberInSurah = 141,
            endPageNumber = 21,
            totalVerses = 148
        ),
        JuzSummary(
            number = 2,
            nameArabic = "الجزء ٢",
            startSurahNumber = 2,
            startSurahNameEnglish = "Al-Baqarah",
            startSurahNameArabic = "سُورَةُ البَقَرَةِ",
            startAyahNumberInSurah = 142,
            startPageNumber = 22,
            endSurahNumber = 2,
            endSurahNameEnglish = "Al-Baqarah",
            endSurahNameArabic = "سُورَةُ البَقَرَةِ",
            endAyahNumberInSurah = 252,
            endPageNumber = 41,
            totalVerses = 111
        ),
        JuzSummary(
            number = 3,
            nameArabic = "الجزء ٣",
            startSurahNumber = 2,
            startSurahNameEnglish = "Al-Baqarah",
            startSurahNameArabic = "سُورَةُ البَقَرَةِ",
            startAyahNumberInSurah = 253,
            startPageNumber = 42,
            endSurahNumber = 3,
            endSurahNameEnglish = "Ali 'Imran",
            endSurahNameArabic = "سُورَةُ آلِ عِمْرَانَ",
            endAyahNumberInSurah = 92,
            endPageNumber = 61,
            totalVerses = 126
        ),
        JuzSummary(
            number = 4,
            nameArabic = "الجزء ٤",
            startSurahNumber = 3,
            startSurahNameEnglish = "Ali 'Imran",
            startSurahNameArabic = "سُورَةُ آلِ عِمْرَانَ",
            startAyahNumberInSurah = 93,
            startPageNumber = 62,
            endSurahNumber = 4,
            endSurahNameEnglish = "An-Nisa",
            endSurahNameArabic = "سُورَةُ النِّسَاءِ",
            endAyahNumberInSurah = 23,
            endPageNumber = 81,
            totalVerses = 131
        ),
        JuzSummary(
            number = 5,
            nameArabic = "الجزء ٥",
            startSurahNumber = 4,
            startSurahNameEnglish = "An-Nisa",
            startSurahNameArabic = "سُورَةُ النِّسَاءِ",
            startAyahNumberInSurah = 24,
            startPageNumber = 82,
            endSurahNumber = 4,
            endSurahNameEnglish = "An-Nisa",
            endSurahNameArabic = "سُورَةُ النِّسَاءِ",
            endAyahNumberInSurah = 147,
            endPageNumber = 101,
            totalVerses = 124
        ),
        JuzSummary(
            number = 6,
            nameArabic = "الجزء ٦",
            startSurahNumber = 4,
            startSurahNameEnglish = "An-Nisa",
            startSurahNameArabic = "سُورَةُ النِّسَاءِ",
            startAyahNumberInSurah = 148,
            startPageNumber = 102,
            endSurahNumber = 5,
            endSurahNameEnglish = "Al-Ma'idah",
            endSurahNameArabic = "سُورَةُ المَائِدَةِ",
            endAyahNumberInSurah = 81,
            endPageNumber = 121,
            totalVerses = 110
        ),
        JuzSummary(
            number = 7,
            nameArabic = "الجزء ٧",
            startSurahNumber = 5,
            startSurahNameEnglish = "Al-Ma'idah",
            startSurahNameArabic = "سُورَةُ المَائِدَةِ",
            startAyahNumberInSurah = 82,
            startPageNumber = 122,
            endSurahNumber = 6,
            endSurahNameEnglish = "Al-An'am",
            endSurahNameArabic = "سُورَةُ الأَنْعَامِ",
            endAyahNumberInSurah = 110,
            endPageNumber = 141,
            totalVerses = 149
        ),
        JuzSummary(
            number = 8,
            nameArabic = "الجزء ٨",
            startSurahNumber = 6,
            startSurahNameEnglish = "Al-An'am",
            startSurahNameArabic = "سُورَةُ الأَنْعَامِ",
            startAyahNumberInSurah = 111,
            startPageNumber = 142,
            endSurahNumber = 7,
            endSurahNameEnglish = "Al-A'raf",
            endSurahNameArabic = "سُورَةُ الأَعْرَافِ",
            endAyahNumberInSurah = 87,
            endPageNumber = 161,
            totalVerses = 142
        ),
        JuzSummary(
            number = 9,
            nameArabic = "الجزء ٩",
            startSurahNumber = 7,
            startSurahNameEnglish = "Al-A'raf",
            startSurahNameArabic = "سُورَةُ الأَعْرَافِ",
            startAyahNumberInSurah = 88,
            startPageNumber = 162,
            endSurahNumber = 8,
            endSurahNameEnglish = "Al-Anfal",
            endSurahNameArabic = "سُورَةُ الأَنْفَالِ",
            endAyahNumberInSurah = 40,
            endPageNumber = 181,
            totalVerses = 159
        ),
        JuzSummary(
            number = 10,
            nameArabic = "الجزء ١٠",
            startSurahNumber = 8,
            startSurahNameEnglish = "Al-Anfal",
            startSurahNameArabic = "سُورَةُ الأَنْفَالِ",
            startAyahNumberInSurah = 41,
            startPageNumber = 182,
            endSurahNumber = 9,
            endSurahNameEnglish = "At-Tawbah",
            endSurahNameArabic = "سُورَةُ التَّوْبَةِ",
            endAyahNumberInSurah = 92,
            endPageNumber = 201,
            totalVerses = 127
        ),
        JuzSummary(
            number = 11,
            nameArabic = "الجزء ١١",
            startSurahNumber = 9,
            startSurahNameEnglish = "At-Tawbah",
            startSurahNameArabic = "سُورَةُ التَّوْبَةِ",
            startAyahNumberInSurah = 93,
            startPageNumber = 202,
            endSurahNumber = 11,
            endSurahNameEnglish = "Hud",
            endSurahNameArabic = "سُورَةُ هُودٍ",
            endAyahNumberInSurah = 5,
            endPageNumber = 221,
            totalVerses = 151
        ),
        JuzSummary(
            number = 12,
            nameArabic = "الجزء ١٢",
            startSurahNumber = 11,
            startSurahNameEnglish = "Hud",
            startSurahNameArabic = "سُورَةُ هُودٍ",
            startAyahNumberInSurah = 6,
            startPageNumber = 222,
            endSurahNumber = 12,
            endSurahNameEnglish = "Yusuf",
            endSurahNameArabic = "سُورَةُ يُوسُفَ",
            endAyahNumberInSurah = 52,
            endPageNumber = 241,
            totalVerses = 170
        ),
        JuzSummary(
            number = 13,
            nameArabic = "الجزء ١٣",
            startSurahNumber = 12,
            startSurahNameEnglish = "Yusuf",
            startSurahNameArabic = "سُورَةُ يُوسُفَ",
            startAyahNumberInSurah = 53,
            startPageNumber = 242,
            endSurahNumber = 14,
            endSurahNameEnglish = "Ibrahim",
            endSurahNameArabic = "سُورَةُ إِبْرَاهِيمَ",
            endAyahNumberInSurah = 52,
            endPageNumber = 261,
            totalVerses = 155
        ),
        JuzSummary(
            number = 14,
            nameArabic = "الجزء ١٤",
            startSurahNumber = 15,
            startSurahNameEnglish = "Al-Hijr",
            startSurahNameArabic = "سُورَةُ الحِجْرِ",
            startAyahNumberInSurah = 1,
            startPageNumber = 262,
            endSurahNumber = 16,
            endSurahNameEnglish = "An-Nahl",
            endSurahNameArabic = "سُورَةُ النَّحْلِ",
            endAyahNumberInSurah = 128,
            endPageNumber = 281,
            totalVerses = 227
        ),
        JuzSummary(
            number = 15,
            nameArabic = "الجزء ١٥",
            startSurahNumber = 17,
            startSurahNameEnglish = "Al-Isra",
            startSurahNameArabic = "سُورَةُ الإِسْرَاءِ",
            startAyahNumberInSurah = 1,
            startPageNumber = 282,
            endSurahNumber = 18,
            endSurahNameEnglish = "Al-Kahf",
            endSurahNameArabic = "سُورَةُ الكَهْفِ",
            endAyahNumberInSurah = 74,
            endPageNumber = 301,
            totalVerses = 185
        ),
        JuzSummary(
            number = 16,
            nameArabic = "الجزء ١٦",
            startSurahNumber = 18,
            startSurahNameEnglish = "Al-Kahf",
            startSurahNameArabic = "سُورَةُ الكَهْفِ",
            startAyahNumberInSurah = 75,
            startPageNumber = 302,
            endSurahNumber = 20,
            endSurahNameEnglish = "Ta-Ha",
            endSurahNameArabic = "سُورَةُ طه",
            endAyahNumberInSurah = 135,
            endPageNumber = 321,
            totalVerses = 269
        ),
        JuzSummary(
            number = 17,
            nameArabic = "الجزء ١٧",
            startSurahNumber = 21,
            startSurahNameEnglish = "Al-Anbiya",
            startSurahNameArabic = "سُورَةُ الأَنْبِيَاءِ",
            startAyahNumberInSurah = 1,
            startPageNumber = 322,
            endSurahNumber = 22,
            endSurahNameEnglish = "Al-Hajj",
            endSurahNameArabic = "سُورَةُ الحَجِّ",
            endAyahNumberInSurah = 78,
            endPageNumber = 341,
            totalVerses = 190
        ),
        JuzSummary(
            number = 18,
            nameArabic = "الجزء ١٨",
            startSurahNumber = 23,
            startSurahNameEnglish = "Al-Mu'minun",
            startSurahNameArabic = "سُورَةُ المُؤْمِنُونَ",
            startAyahNumberInSurah = 1,
            startPageNumber = 342,
            endSurahNumber = 25,
            endSurahNameEnglish = "Al-Furqan",
            endSurahNameArabic = "سُورَةُ الفُرْقَانِ",
            endAyahNumberInSurah = 20,
            endPageNumber = 361,
            totalVerses = 202
        ),
        JuzSummary(
            number = 19,
            nameArabic = "الجزء ١٩",
            startSurahNumber = 25,
            startSurahNameEnglish = "Al-Furqan",
            startSurahNameArabic = "سُورَةُ الفُرْقَانِ",
            startAyahNumberInSurah = 21,
            startPageNumber = 362,
            endSurahNumber = 27,
            endSurahNameEnglish = "An-Naml",
            endSurahNameArabic = "سُورَةُ النَّمْلِ",
            endAyahNumberInSurah = 55,
            endPageNumber = 381,
            totalVerses = 339
        ),
        JuzSummary(
            number = 20,
            nameArabic = "الجزء ٢٠",
            startSurahNumber = 27,
            startSurahNameEnglish = "An-Naml",
            startSurahNameArabic = "سُورَةُ النَّمْلِ",
            startAyahNumberInSurah = 56,
            startPageNumber = 382,
            endSurahNumber = 29,
            endSurahNameEnglish = "Al-'Ankabut",
            endSurahNameArabic = "سُورَةُ العَنْكَبُوتِ",
            endAyahNumberInSurah = 45,
            endPageNumber = 401,
            totalVerses = 171
        ),
        JuzSummary(
            number = 21,
            nameArabic = "الجزء ٢١",
            startSurahNumber = 29,
            startSurahNameEnglish = "Al-'Ankabut",
            startSurahNameArabic = "سُورَةُ العَنْكَبُوتِ",
            startAyahNumberInSurah = 46,
            startPageNumber = 402,
            endSurahNumber = 33,
            endSurahNameEnglish = "Al-Ahzab",
            endSurahNameArabic = "سُورَةُ الأَحْزَابِ",
            endAyahNumberInSurah = 30,
            endPageNumber = 421,
            totalVerses = 178
        ),
        JuzSummary(
            number = 22,
            nameArabic = "الجزء ٢٢",
            startSurahNumber = 33,
            startSurahNameEnglish = "Al-Ahzab",
            startSurahNameArabic = "سُورَةُ الأَحْزَابِ",
            startAyahNumberInSurah = 31,
            startPageNumber = 422,
            endSurahNumber = 36,
            endSurahNameEnglish = "Ya-Sin",
            endSurahNameArabic = "سُورَةُ يسٓ",
            endAyahNumberInSurah = 27,
            endPageNumber = 441,
            totalVerses = 169
        ),
        JuzSummary(
            number = 23,
            nameArabic = "الجزء ٢٣",
            startSurahNumber = 36,
            startSurahNameEnglish = "Ya-Sin",
            startSurahNameArabic = "سُورَةُ يسٓ",
            startAyahNumberInSurah = 28,
            startPageNumber = 442,
            endSurahNumber = 39,
            endSurahNameEnglish = "Az-Zumar",
            endSurahNameArabic = "سُورَةُ الزُّمَرِ",
            endAyahNumberInSurah = 31,
            endPageNumber = 461,
            totalVerses = 357
        ),
        JuzSummary(
            number = 24,
            nameArabic = "الجزء ٢٤",
            startSurahNumber = 39,
            startSurahNameEnglish = "Az-Zumar",
            startSurahNameArabic = "سُورَةُ الزُّمَرِ",
            startAyahNumberInSurah = 32,
            startPageNumber = 462,
            endSurahNumber = 41,
            endSurahNameEnglish = "Fussilat",
            endSurahNameArabic = "سُورَةُ فُصِّلَتْ",
            endAyahNumberInSurah = 46,
            endPageNumber = 481,
            totalVerses = 175
        ),
        JuzSummary(
            number = 25,
            nameArabic = "الجزء ٢٥",
            startSurahNumber = 41,
            startSurahNameEnglish = "Fussilat",
            startSurahNameArabic = "سُورَةُ فُصِّلَتْ",
            startAyahNumberInSurah = 47,
            startPageNumber = 482,
            endSurahNumber = 45,
            endSurahNameEnglish = "Al-Jathiyah",
            endSurahNameArabic = "سُورَةُ الجَاثِيَةِ",
            endAyahNumberInSurah = 37,
            endPageNumber = 501,
            totalVerses = 246
        ),
        JuzSummary(
            number = 26,
            nameArabic = "الجزء ٢٦",
            startSurahNumber = 46,
            startSurahNameEnglish = "Al-Ahqaf",
            startSurahNameArabic = "سُورَةُ الأَحْقَافِ",
            startAyahNumberInSurah = 1,
            startPageNumber = 502,
            endSurahNumber = 51,
            endSurahNameEnglish = "Adh-Dhariyat",
            endSurahNameArabic = "سُورَةُ الذَّارِيَاتِ",
            endAyahNumberInSurah = 30,
            endPageNumber = 521,
            totalVerses = 195
        ),
        JuzSummary(
            number = 27,
            nameArabic = "الجزء ٢٧",
            startSurahNumber = 51,
            startSurahNameEnglish = "Adh-Dhariyat",
            startSurahNameArabic = "سُورَةُ الذَّارِيَاتِ",
            startAyahNumberInSurah = 31,
            startPageNumber = 522,
            endSurahNumber = 57,
            endSurahNameEnglish = "Al-Hadid",
            endSurahNameArabic = "سُورَةُ الحَدِيدِ",
            endAyahNumberInSurah = 29,
            endPageNumber = 541,
            totalVerses = 399
        ),
        JuzSummary(
            number = 28,
            nameArabic = "الجزء ٢٨",
            startSurahNumber = 58,
            startSurahNameEnglish = "Al-Mujadila",
            startSurahNameArabic = "سُورَةُ المُجَادِلَةِ",
            startAyahNumberInSurah = 1,
            startPageNumber = 542,
            endSurahNumber = 66,
            endSurahNameEnglish = "At-Tahrim",
            endSurahNameArabic = "سُورَةُ التَّحْرِيمِ",
            endAyahNumberInSurah = 12,
            endPageNumber = 561,
            totalVerses = 137
        ),
        JuzSummary(
            number = 29,
            nameArabic = "الجزء ٢٩",
            startSurahNumber = 67,
            startSurahNameEnglish = "Al-Mulk",
            startSurahNameArabic = "سُورَةُ المُلْكِ",
            startAyahNumberInSurah = 1,
            startPageNumber = 562,
            endSurahNumber = 77,
            endSurahNameEnglish = "Al-Mursalat",
            endSurahNameArabic = "سُورَةُ المُرْسَلَاتِ",
            endAyahNumberInSurah = 50,
            endPageNumber = 581,
            totalVerses = 431
        ),
        JuzSummary(
            number = 30,
            nameArabic = "الجزء ٣٠",
            startSurahNumber = 78,
            startSurahNameEnglish = "An-Naba",
            startSurahNameArabic = "سُورَةُ النَّبَإِ",
            startAyahNumberInSurah = 1,
            startPageNumber = 582,
            endSurahNumber = 114,
            endSurahNameEnglish = "An-Nas",
            endSurahNameArabic = "سُورَةُ النَّاسِ",
            endAyahNumberInSurah = 6,
            endPageNumber = 604,
            totalVerses = 564
        )
    )

    /**
     * Resolves a [JuzSummary] by its 1-indexed number (`1..30`).
     *
     * @param number The Juz ordinal number.
     * @return The corresponding [JuzSummary], or `null` if the number is out of bounds.
     */
    fun getJuzByNumber(number: Int): JuzSummary? {
        if (number !in 1..30) return null
        return allJuz.getOrNull(number - 1)
    }

    /**
     * Resolves the [JuzSummary] corresponding to a Medina Mushaf page number (`1..604`).
     *
     * @param pageNumber The 1-indexed Medina Mushaf page number.
     * @return The [JuzSummary] containing this page. Falls back to Juz 1 or Juz 30 if out of range.
     */
    fun getJuzForPage(pageNumber: Int): JuzSummary {
        val clamped = pageNumber.coerceIn(1, 604)
        return allJuz.find { clamped in it.startPageNumber..it.endPageNumber } ?: allJuz.last()
    }
}

