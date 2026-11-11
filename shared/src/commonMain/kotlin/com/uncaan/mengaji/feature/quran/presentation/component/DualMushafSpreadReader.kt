package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.core.ui.EXPANDED_SPINE_GUTTER
import com.uncaan.mengaji.feature.quran.presentation.QuranReaderState
import com.uncaan.mengaji.feature.quran.presentation.QuranUiAction
import com.uncaan.mengaji.theme.MeNgajiTheme

/**
 * Authentic 604-page Medina Mushaf dual-page book spread reader (CMP-606).
 *
 * Implements Figma Screen 04 (`node-id=1-200` & `1:244`) for expanded tablet, desktop, and foldable
 * displays (`>= 840.dp`). Emulates an authentic open physical Quran:
 * - **Right Leaf (Odd Page):** Displays page $2k + 1$ (e.g. Page 1 *Al-Fatihah*).
 * - **Left Leaf (Even Page):** Displays facing page $2k + 2$ (e.g. Page 2 *Al-Baqarah* 1–5).
 * - **Central Spine Shadow & Hinge Gutter:** A 20.dp gutter with subtle depth gradients preventing
 *   occlusion on foldable device creases.
 * - **RTL Horizontal Pager:** 302 canonical spreads (`0..301`) advancing pages $N \to N+1$ with
 *   natural Arabic Right-to-Left swiping.
 * - **Recitation Highlighting:** Highlights active audio verses on whichever facing page they reside.
 *
 * @param readerState Current immutable reader state containing loaded [mushafPage] and [facingMushafPage].
 * @param audioState Current recitation playback state from the audio engine.
 * @param onAction Action dispatcher for spread loading, verse actions, and Khushu mode toggling.
 * @param modifier Layout modifier applied to the dual-spread container.
 * @see MushafPageCanvas
 * @see QuranReaderState
 * @see QuranUiAction
 */
@Composable
fun DualMushafSpreadReader(
    readerState: QuranReaderState,
    audioState: AudioState,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // 302 canonical dual-page spreads in the Medina Mushaf (0-indexed: 0..301)
    val initialSpread = ((readerState.currentPageNumber - 1) / 2).coerceIn(0, 301)
    val pagerState = rememberPagerState(
        initialPage = initialSpread,
        pageCount = { 302 }
    )

    // Synchronize pager position when external state changes (e.g. Quick Jump or Surah click)
    LaunchedEffect(readerState.currentPageNumber) {
        val targetSpread = ((readerState.currentPageNumber - 1) / 2).coerceIn(0, 301)
        if (pagerState.currentPage != targetSpread) {
            pagerState.scrollToPage(targetSpread)
        }
    }

    // Trigger concurrent spread loading when the user swipes to a new spread or either leaf is missing
    LaunchedEffect(
        pagerState,
        readerState.mushafPage?.pageNumber,
        readerState.facingMushafPage?.pageNumber
    ) {
        snapshotFlow { pagerState.currentPage }.collect { spreadIndex ->
            val pageOdd = spreadIndex * 2 + 1
            val pageEven = (pageOdd + 1).coerceAtMost(604)
            val hasOddPage = readerState.mushafPage?.pageNumber == pageOdd ||
                readerState.facingMushafPage?.pageNumber == pageOdd
            val hasEvenPage = readerState.facingMushafPage?.pageNumber == pageEven ||
                readerState.mushafPage?.pageNumber == pageEven
            if ((readerState.currentPageNumber != pageOdd && readerState.currentPageNumber != pageEven) ||
                ((!hasOddPage || !hasEvenPage) && !readerState.isReaderLoading && !readerState.isFacingPageLoading && readerState.readerError == null)
            ) {
                onAction(QuranUiAction.LoadMushafSpread(spreadIndex))
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
        ) { spreadIndex ->
            val pageOdd = spreadIndex * 2 + 1
            val pageEven = (pageOdd + 1).coerceAtMost(604)

            val rightPageData = if (readerState.mushafPage?.pageNumber == pageOdd) {
                readerState.mushafPage
            } else if (readerState.facingMushafPage?.pageNumber == pageOdd) {
                readerState.facingMushafPage
            } else null

            val leftPageData = if (readerState.facingMushafPage?.pageNumber == pageEven) {
                readerState.facingMushafPage
            } else if (readerState.mushafPage?.pageNumber == pageEven) {
                readerState.mushafPage
            } else null

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // -------------------------------------------------------------
                // Right Leaf: Odd Medina Mushaf Page (2k + 1)
                // -------------------------------------------------------------
                val isCurrentSpread = readerState.currentPageNumber == pageOdd || readerState.currentPageNumber == pageEven
                val spreadError = if (isCurrentSpread) readerState.readerError else null

                MushafPageCanvas(
                    pageNumber = pageOdd,
                    pageData = rightPageData,
                    isLoading = rightPageData == null && (readerState.isReaderLoading || spreadError == null),
                    headerBadgeText = if (pageOdd == 1) "RIGHT PANE • ODD PAGE 1 (START)" else "RIGHT PANE • ODD PAGE $pageOdd",
                    highlightGlobalAyahNumber = audioState.currentGlobalAyahNumber,
                    footerText = "— Page $pageOdd (Odd Right Page) —",
                    errorMessage = spreadError,
                    onRetry = { onAction(QuranUiAction.LoadMushafSpread(spreadIndex)) },
                    onAyahClicked = { ayah ->
                        onAction(QuranUiAction.SelectSpreadAyah(ayah, isFacingPage = false))
                    },
                    onToggleKhushu = { onAction(QuranUiAction.ToggleKhushuMode) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                // -------------------------------------------------------------
                // Central Spine Shadow & Foldable Hinge Gutter (20.dp)
                // -------------------------------------------------------------
                Box(
                    modifier = Modifier
                        .width(EXPANDED_SPINE_GUTTER)
                        .fillMaxHeight()
                        .padding(vertical = 12.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.12f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.12f)
                                )
                            )
                        )
                )

                // -------------------------------------------------------------
                // Left Leaf: Even Medina Mushaf Page (2k + 2)
                // -------------------------------------------------------------
                MushafPageCanvas(
                    pageNumber = pageEven,
                    pageData = leftPageData,
                    isLoading = leftPageData == null && (readerState.isFacingPageLoading || spreadError == null),
                    headerBadgeText = "LEFT PANE • EVEN PAGE $pageEven",
                    highlightGlobalAyahNumber = audioState.currentGlobalAyahNumber,
                    footerText = "— Page $pageEven (Even Left Page) —",
                    errorMessage = spreadError,
                    onRetry = { onAction(QuranUiAction.LoadMushafSpread(spreadIndex)) },
                    onAyahClicked = { ayah ->
                        onAction(QuranUiAction.SelectSpreadAyah(ayah, isFacingPage = true))
                    },
                    onToggleKhushu = { onAction(QuranUiAction.ToggleKhushuMode) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Preview
@Composable
private fun DualMushafSpreadReaderPreview() {
    MeNgajiTheme {
        DualMushafSpreadReader(
            readerState = QuranReaderState(),
            audioState = AudioState.IDLE,
            onAction = {}
        )
    }
}

