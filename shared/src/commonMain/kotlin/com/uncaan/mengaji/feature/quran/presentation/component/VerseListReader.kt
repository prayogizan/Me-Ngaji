package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.presentation.QuranReaderState
import com.uncaan.mengaji.feature.quran.presentation.QuranUiAction
import com.uncaan.mengaji.theme.PrimaryGreen
import com.uncaan.mengaji.theme.SecondaryGold
import com.uncaan.mengaji.theme.getQuranArabicTextStyle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

/**
 * Vertical verse-by-verse study reader component (Figma `Screen_02_Compact_Verse_List_Reader`).
 *
 * Implements:
 * - Ornamental calligraphy header with Surah metadata and canonical 3-way Bismillah banner rules.
 * - Active reciting Ayah karaoke highlighting with `2.dp` Gold border and warm cream background tint.
 * - Automated smooth scrolling tracking active [AudioState] track transitions.
 * - Debounced (`800ms`) reading position tracking automatically persisting recitation checkpoints.
 * - Contextual `•••` action sheet triggers for per-verse operations.
 *
 * @param readerState Current immutable reader state holding [SurahDetail] and active coordinates.
 * @param audioState Reactive audio engine state for active verse synchronization.
 * @param onAction Action dispatcher for verse operations, mode toggles, and navigation.
 * @param modifier Layout modifier applied to the root container.
 * @see SurahDetail
 * @see Ayah
 * @see AudioState
 * @see QuranUiAction
 */
@OptIn(FlowPreview::class)
@Composable
fun VerseListReader(
    readerState: QuranReaderState,
    audioState: AudioState,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val surahDetail = readerState.surahDetail ?: return
    val ayahs = surahDetail.ayahs
    val lazyListState = rememberLazyListState()

    // Smooth auto-scroll when active audio recitation transitions to a new verse
    LaunchedEffect(audioState.currentGlobalAyahNumber, audioState.currentAyahNumberInSurah) {
        val targetAyahNumber = audioState.currentAyahNumberInSurah
        if (targetAyahNumber != null && audioState.currentSurahNumber == surahDetail.number) {
            val targetIndex = ayahs.indexOfFirst { it.numberInSurah == targetAyahNumber }
            if (targetIndex >= 0) {
                // +1 offset accounts for the header item at index 0
                lazyListState.animateScrollToItem((targetIndex + 1).coerceAtMost(ayahs.size))
            }
        }
    }

    // Scroll to initial requested Ayah when reader first opens
    LaunchedEffect(readerState.currentSurahNumber) {
        val targetIndex = ayahs.indexOfFirst { it.numberInSurah == readerState.currentAyahNumber }
        if (targetIndex > 0) {
            lazyListState.scrollToItem(targetIndex + 1)
        }
    }

    // Debounced automatic LastRead progress recording during manual scrolling
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .debounce(800)
            .collectLatest { index ->
                val adjustedIndex = index - 1
                if (adjustedIndex in ayahs.indices) {
                    val visibleAyah = ayahs[adjustedIndex]
                    onAction(QuranUiAction.SaveManualLastRead(visibleAyah))
                }
            }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Item 0: Ornamental Surah Header & Bismillah Banner
        item(key = "surah_header_${surahDetail.number}") {
            SurahOrnamentalHeader(
                surahDetail = surahDetail,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Items 1..N: Verse Cards
        itemsIndexed(
            items = ayahs,
            key = { _, ayah -> "ayah_${ayah.number}" }
        ) { _, ayah ->
            val isCurrentReciting = audioState.isPlaying &&
                (audioState.currentGlobalAyahNumber == ayah.number ||
                    (audioState.currentSurahNumber == ayah.surahNumber &&
                        audioState.currentAyahNumberInSurah == ayah.numberInSurah))

            VerseListItemCard(
                ayah = ayah,
                surahNumber = surahDetail.number,
                isReciting = isCurrentReciting,
                onActionClicked = { onAction(QuranUiAction.ShowAyahActionSheet(ayah)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Bottom spacing so audio dock does not obscure final verse
        item(key = "reader_bottom_spacer") {
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}

/**
 * Ornamental header card rendering chapter calligraphy and canonical Bismillah banners.
 *
 * Enforces the canonical Bismillah rules:
 * - Surah 9 (*At-Tawbah*): Never render ornamental Bismillah banner.
 * - Surah 1 (*Al-Fatihah*): Ayah 1 is Bismillah itself, omitted from header.
 * - Surahs 2..8 and 10..114: Dedicated ornamental Bismillah calligraphy banner rendered.
 *
 * @param surahDetail Domain model of the active Surah.
 * @param modifier Layout modifier applied to the header card.
 */
@Composable
fun SurahOrnamentalHeader(
    surahDetail: SurahDetail,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Surah Arabic Calligraphy Title
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = surahDetail.name,
                    style = getQuranArabicTextStyle().copy(fontSize = 28.sp),
                    color = PrimaryGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${surahDetail.number}. ${surahDetail.englishName} (${surahDetail.englishNameTranslation})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${surahDetail.revelationType.displayName} • ${surahDetail.numberOfAyahs} Verses • KeepScreenOn Active ☀️",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Canonical Bismillah Banner: Omitted for Surah 1 (Al-Fatihah) and Surah 9 (At-Tawbah)
            if (surahDetail.number != 1 && surahDetail.number != 9) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(14.dp))

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                        style = getQuranArabicTextStyle().copy(fontSize = 22.sp),
                        color = PrimaryGreen,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Single verse study card component matching Figma `AyahCard_1_2_Playing` (node `1:122`).
 *
 * @param ayah The [Ayah] domain entity to render.
 * @param surahNumber Parent chapter ordinal index (`1..114`).
 * @param isReciting True if this verse is currently being actively played by the audio engine.
 * @param onActionClicked Callback dispatched when the `•••` action button is tapped.
 * @param modifier Layout modifier applied to the card.
 */
@Composable
fun VerseListItemCard(
    ayah: Ayah,
    surahNumber: Int,
    isReciting: Boolean,
    onActionClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isReciting) SecondaryGold else Color.Transparent,
        label = "VerseBorderColor"
    )
    val containerColor = if (isReciting) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isReciting) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isReciting) 5.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Verse Header Row: Number Pill + Reciting Badge + Actions Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Verse Coordinate Pill (e.g. 1:2 • P.1)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryGreen.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${ayah.surahNumber}:${ayah.numberInSurah} • P.${ayah.page}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = PrimaryGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (isReciting) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SecondaryGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🔊 RECITING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                ),
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Action Menu Button (•••)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clickable(onClick = onActionClicked)
                ) {
                    Text(
                        text = "•••",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Uthmani Arabic Text (RTL) with stripped leading Bismillah if non-Fatihah Ayah 1
            val displayArabicText = stripLeadingBismillah(
                text = ayah.text,
                surahNumber = surahNumber,
                ayahNumberInSurah = ayah.numberInSurah
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp)
                ) {
                    Text(
                        text = "$displayArabicText ۝${toArabicIndic(ayah.numberInSurah)}",
                        style = getQuranArabicTextStyle().copy(fontSize = 24.sp, lineHeight = 44.sp),
                        color = PrimaryGreen,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LTR Translation Text
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = ayah.translation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Strips prepended Bismillah strings from verse text for Surahs 2..114 (excluding Surah 9).
 */
fun stripLeadingBismillah(text: String, surahNumber: Int, ayahNumberInSurah: Int): String {
    if (surahNumber == 1 || surahNumber == 9 || ayahNumberInSurah != 1) return text
    val bismillahRegex = Regex("""^بِسْمِ\s+ٱللَّهِ\s+ٱلرَّحْمَٰنِ\s+ٱلرَّحِيمِ\s*|^بِسْمِ\s+ٱللَّهِ\s+ٱلرَّحْمَٰنِ\s+ٱلرَّحِيمِ\s*""")
    return text.replace(bismillahRegex, "").trimStart()
}

/**
 * Converts Western digits to Eastern Arabic-Indic numerals for verse end medallions.
 */
fun toArabicIndic(number: Int): String {
    val westernDigits = "0123456789"
    val arabicIndicDigits = "٠١٢٣٤٥٦٧٨٩"
    return number.toString().map { char ->
        val index = westernDigits.indexOf(char)
        if (index >= 0) arabicIndicDigits[index] else char
    }.joinToString("")
}
