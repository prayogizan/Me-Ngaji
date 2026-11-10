package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.presentation.QuranReaderState
import com.uncaan.mengaji.feature.quran.presentation.QuranUiAction
import com.uncaan.mengaji.theme.PrimaryGreen

/**
 * Top navigation and mode-switching app bar for the Quran recitation reader interface.
 *
 * Implements the Figma Compact Reader header specifications (`Screen_02_Compact_Verse_List_Reader`
 * and `Screen_03_Compact_Mushaf_Page_BottomSheet`). Automatically conceals with slide/fade
 * transitions when distraction-free *Khushu'* mode is active.
 *
 * @param readerState Current immutable reader state holding chapter, page, and mode metadata.
 * @param onAction Action dispatcher for navigating back, toggling modes, and opening Quick Jump.
 * @param modifier Layout modifier applied to the root container.
 * @param showBackButton True if the "← Surah" back button should be rendered (for single-pane layouts).
 * @param showSidebarToggle True if the sidebar expansion toggle button should be rendered (for multi-pane layouts).
 * @param isSidebarVisible True if the master catalog sidebar is currently expanded.
 * @see QuranReaderState
 * @see QuranUiAction
 * @see ReaderMode
 */
@Composable
fun ReaderTopBar(
    readerState: QuranReaderState,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    showSidebarToggle: Boolean = false,
    isSidebarVisible: Boolean = true
) {
    AnimatedVisibility(
        visible = !readerState.isKhushuMode,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Navigation Leading Action: Back to Catalog or Toggle Master Sidebar
                    if (showSidebarToggle) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { onAction(QuranUiAction.ToggleCatalogSidebar) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isSidebarVisible) "⫷ Index" else "⫸ Index",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    color = PrimaryGreen
                                )
                            }
                        }
                    } else if (showBackButton) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { onAction(QuranUiAction.BackToCatalog) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "← Surah",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }

                    // Surah / Page Center Title & Subtitle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        val titleText = if (readerState.readerMode == ReaderMode.VERSE_LIST) {
                            val surah = readerState.surahDetail
                            if (surah != null) "${surah.number}. ${surah.englishName}" else "Surah ${readerState.currentSurahNumber}"
                        } else {
                            "Mushaf Page ${readerState.currentPageNumber}"
                        }

                        val subtitleText = if (readerState.readerMode == ReaderMode.VERSE_LIST) {
                            val detail = readerState.surahDetail
                            val ayah = detail?.ayahs?.find { it.numberInSurah == readerState.currentAyahNumber }
                            val page = ayah?.page ?: readerState.currentPageNumber
                            val juz = ayah?.juz ?: 1
                            "Page $page • Juz $juz • ${detail?.numberOfAyahs ?: 0} Verses"
                        } else {
                            val page = readerState.mushafPage
                            val surahsStr = page?.surahs?.joinToString(", ") { it.englishName } ?: ""
                            "Juz ${page?.juzNumber ?: 1}${if (surahsStr.isNotBlank()) " • $surahsStr" else ""}"
                        }

                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitleText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Mode Switcher Segmented Control
                    ReaderModeSegmentedControl(
                        activeMode = readerState.readerMode,
                        onModeSelected = { onAction(QuranUiAction.SwitchReaderMode(it)) }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom row with Quick Jump shortcut pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        modifier = Modifier.clickable { onAction(QuranUiAction.SetQuickJumpSheetVisible(true)) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Quick Jump",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = PrimaryGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Segmented toggle control between [ReaderMode.VERSE_LIST] and [ReaderMode.MUSHAF_PAGE].
 *
 * @param activeMode Currently active reader presentation mode.
 * @param onModeSelected Callback dispatched when a mode segment is tapped.
 * @param modifier Layout modifier applied to the segmented container.
 */
@Composable
fun ReaderModeSegmentedControl(
    activeMode: ReaderMode,
    onModeSelected: (ReaderMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReaderModeSegment(
            label = "List",
            isSelected = activeMode == ReaderMode.VERSE_LIST,
            onClick = { onModeSelected(ReaderMode.VERSE_LIST) }
        )
        ReaderModeSegment(
            label = "Mushaf",
            isSelected = activeMode == ReaderMode.MUSHAF_PAGE,
            onClick = { onModeSelected(ReaderMode.MUSHAF_PAGE) }
        )
    }
}

@Composable
private fun ReaderModeSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen else Color.Transparent,
        label = "SegmentBackground"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "SegmentText"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = textColor
        )
    }
}

