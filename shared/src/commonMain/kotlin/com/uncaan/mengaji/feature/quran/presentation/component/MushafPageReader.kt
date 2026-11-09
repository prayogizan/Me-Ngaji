package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.presentation.QuranReaderState
import com.uncaan.mengaji.feature.quran.presentation.QuranUiAction
import com.uncaan.mengaji.theme.PrimaryGreen
import com.uncaan.mengaji.theme.SecondaryGold
import com.uncaan.mengaji.theme.getQuranArabicTextStyle

/**
 * Authentic 604-page RTL Medina Mushaf pager (Figma `Screen_03_Compact_Mushaf_Page_BottomSheet`).
 *
 * Implements:
 * - RTL `HorizontalPager` advancing pages $N \to N+1$ with natural left-to-right swipe motion.
 * - Illuminated double-bordered Mushaf frame on warm cream `#FFFDF7` (`#0A2E24` in dark mode).
 * - Inline chapter banners when a new Surah begins on the page.
 * - End-of-ayah calligraphy medallions (`۝`) embedded within continuous flowing text.
 * - Tap-to-toggle distraction-free *Khushu'* immersive mode.
 * - Long-press and click interaction to trigger contextual [AyahActionBottomSheet] actions.
 *
 * @param readerState Current immutable reader state holding [MushafPage] data and page index.
 * @param onAction Action dispatcher for page loads, mode toggling, and verse selection.
 * @param modifier Layout modifier applied to the root container.
 * @see MushafPage
 * @see QuranReaderState
 * @see QuranUiAction
 */
@Composable
fun MushafPageReader(
    readerState: QuranReaderState,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // 604 canonical pages in the Medina Mushaf (0-indexed in pager: 0..603)
    val initialPage = (readerState.currentPageNumber - 1).coerceIn(0, 603)
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { 604 }
    )

    // Synchronize pager position when external state changes (e.g. from Quick Jump or Mode Switch)
    LaunchedEffect(readerState.currentPageNumber) {
        val targetIndex = (readerState.currentPageNumber - 1).coerceIn(0, 603)
        if (pagerState.currentPage != targetIndex) {
            pagerState.scrollToPage(targetIndex)
        }
    }

    // Load new Mushaf page whenever the user swipes to a new page
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { index ->
            val pageNumber = index + 1
            if (readerState.currentPageNumber != pageNumber) {
                onAction(QuranUiAction.LoadMushafPage(pageNumber))
            }
        }
    }

    // Wrap pager in RTL layout direction for authentic Arabic book pagination
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        HorizontalPager(
            state = pagerState,
            reverseLayout = true,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) { pageIndex ->
            val pageNumber = pageIndex + 1
            val pageData = if (readerState.currentPageNumber == pageNumber) readerState.mushafPage else null

            MushafPageCanvas(
                pageNumber = pageNumber,
                pageData = pageData,
                isLoading = readerState.isReaderLoading && pageData == null,
                onAyahClicked = { onAction(QuranUiAction.ShowAyahActionSheet(it)) },
                onToggleKhushu = { onAction(QuranUiAction.ToggleKhushuMode) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

/**
 * Illuminated Medina Mushaf page canvas rendering ornamental borders, headers, and continuous verses.
 *
 * @param pageNumber The 1-indexed Medina Mushaf page number (`1..604`).
 * @param pageData Loaded [MushafPage] data, or `null` while buffering.
 * @param isLoading True if page data is actively in-flight over network/cache.
 * @param onAyahClicked Callback invoked when a verse in the canvas is selected.
 * @param onToggleKhushu Callback invoked when the canvas background is tapped.
 * @param modifier Layout modifier applied to the canvas frame.
 */
@Composable
fun MushafPageCanvas(
    pageNumber: Int,
    pageData: MushafPage?,
    isLoading: Boolean,
    onAyahClicked: (Ayah) -> Unit,
    onToggleKhushu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Outer Illuminated Mushaf Border (Gold & Emerald Accents)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFDF7)) // Warm Cream Authentic Mushaf Paper
            .border(
                width = 3.dp,
                color = SecondaryGold.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onToggleKhushu)
            .padding(4.dp)
    ) {
        // Inner Ornamental Border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = PrimaryGreen.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            } else if (pageData != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Page Top Header: Juz Title (Left) + Hint (Center) + Surah Title (Right)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الجزء ${toArabicIndic(pageData.juzNumber)}",
                            style = getQuranArabicTextStyle().copy(fontSize = 14.sp),
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "◀ Swipe for Page ${pageNumber + 1} ▶",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF8C7A58)
                        )

                        val firstSurahName = pageData.surahs.firstOrNull()?.name ?: ""
                        Text(
                            text = firstSurahName,
                            style = getQuranArabicTextStyle().copy(fontSize = 14.sp),
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SecondaryGold.copy(alpha = 0.4f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Group verses by Surah to render inline chapter headers when a Surah begins on this page
                    val ayahsBySurah = remember(pageData.ayahs) {
                        pageData.ayahs.groupBy { it.surahNumber }
                    }

                    ayahsBySurah.forEach { (surahNum, ayahs) ->
                        val surahSummary = pageData.surahs.find { it.number == surahNum }
                        val isFirstAyahOfSurahOnPage = ayahs.any { it.numberInSurah == 1 }

                        // Render chapter header frame if Surah starts on this page
                        if (isFirstAyahOfSurahOnPage && surahSummary != null) {
                            InlineSurahHeaderBanner(
                                surahSummary = surahSummary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                        }

                        // Flowing continuous Uthmani verses with end medallions
                        ayahs.forEach { ayah ->
                            val cleanText = stripLeadingBismillah(ayah.text, ayah.surahNumber, ayah.numberInSurah)
                            Surface(
                                color = Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAyahClicked(ayah) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$cleanText ۝${toArabicIndic(ayah.numberInSurah)}",
                                    style = getQuranArabicTextStyle().copy(
                                        fontSize = 22.sp,
                                        lineHeight = 44.sp
                                    ),
                                    color = Color(0xFF1B1C1B),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Page Footer
                    Text(
                        text = "— Page $pageNumber of 604 (Tap canvas to toggle Khushu' immersive mode) —",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = Color(0xFF7A7060)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

/**
 * Ornamental inline chapter banner rendered on Mushaf pages where a new Surah commences.
 */
@Composable
fun InlineSurahHeaderBanner(
    surahSummary: com.uncaan.mengaji.feature.quran.domain.model.SurahSummary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = PrimaryGreen.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryGold.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "۞ سُورَةُ ${surahSummary.name} ۞",
                style = getQuranArabicTextStyle().copy(fontSize = 18.sp),
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (surahSummary.number != 1 && surahSummary.number != 9) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                    style = getQuranArabicTextStyle().copy(fontSize = 16.sp),
                    color = PrimaryGreen,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

