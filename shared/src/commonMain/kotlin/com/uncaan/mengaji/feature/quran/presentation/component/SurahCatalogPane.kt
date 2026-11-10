package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.feature.quran.presentation.QuranCatalogState
import com.uncaan.mengaji.feature.quran.presentation.QuranUiAction
import com.uncaan.mengaji.feature.quran.presentation.model.CatalogTab
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.theme.PrimaryGreen

/**
 * Master catalog composable pane for Quran exploration, embodying Figma Screen 01 (`node-id=1-25`).
 *
 * Coordinates the search bar with instant `surah:ayah` direct detection, the [LastReadHeroCard] with
 * 1-tap recitation resume, the [SegmentedCatalogTabs] 3-way control, scrollable lists for Surahs,
 * Juzs, and saved bookmarks, as well as the [QuickJumpBottomSheet] scrubber.
 *
 * @param catalogState The current immutable [QuranCatalogState] emitted by the ViewModel.
 * @param onAction Callback to dispatch [QuranUiAction] intents to the ViewModel.
 * @param modifier Layout modifier applied to the pane container.
 * @param activeSurahNumber Optional 1-indexed active Surah number to highlight in split-pane mode.
 * @param facingSurahNumber Optional 1-indexed facing Surah number in dual-page spread mode.
 * @param isSplitPane True if rendered as a master sidebar inside medium/expanded multi-pane layouts.
 * @see QuranCatalogState
 * @see QuranUiAction
 * @see LastReadHeroCard
 * @see SegmentedCatalogTabs
 * @see QuickJumpBottomSheet
 */
@Composable
fun SurahCatalogPane(
    catalogState: QuranCatalogState,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier,
    activeSurahNumber: Int? = null,
    facingSurahNumber: Int? = null,
    isSplitPane: Boolean = false
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isSplitPane) 12.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title & Quick Jump / Split-Pane Indicator
            item(key = "header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSplitPane) "Surah & Juz Index" else "MeNgaji • Al-Quran",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isSplitPane) 18.sp else 22.sp
                        ),
                        color = PrimaryGreen
                    )

                    if (isSplitPane) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "Split-Pane",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = PrimaryGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onAction(QuranUiAction.SetQuickJumpSheetVisible(true))
                                }
                        ) {
                            Text(
                                text = "⚡ Quick Jump",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = PrimaryGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar
            item(key = "search_bar") {
                QuranSearchBar(
                    query = catalogState.searchQuery,
                    onQueryChange = { onAction(QuranUiAction.UpdateCatalogSearch(it)) },
                    onSearch = { query ->
                        val target = catalogState.directJumpTarget
                        if (target != null) {
                            onAction(QuranUiAction.JumpToSurah(target.first, target.second))
                        } else {
                            onAction(QuranUiAction.Search(query))
                        }
                    },
                    onClear = { onAction(QuranUiAction.ClearCatalogSearch) }
                )
            }

            // Direct Ayah Jump Chip (if regex matches surah:ayah)
            if (catalogState.directJumpTarget != null) {
                item(key = "direct_jump_chip") {
                    DirectAyahJumpChip(
                        surahNumber = catalogState.directJumpTarget.first,
                        ayahNumber = catalogState.directJumpTarget.second,
                        onJump = { surah, ayah ->
                            onAction(QuranUiAction.JumpToSurah(surah, ayah))
                        }
                    )
                }
            }

            // Last Read Hero Card (if progress exists and not actively filtering)
            if (catalogState.lastReadProgress != null && catalogState.searchQuery.isBlank()) {
                item(key = "last_read_hero") {
                    LastReadHeroCard(
                        progress = catalogState.lastReadProgress,
                        onResume = {
                            onAction(QuranUiAction.ResumeLastRead(catalogState.lastReadProgress))
                        }
                    )
                }
            }

            // Segmented Catalog Tabs
            item(key = "segmented_tabs") {
                SegmentedCatalogTabs(
                    activeTab = catalogState.activeTab,
                    surahCount = catalogState.surahs.size.takeIf { it > 0 } ?: 114,
                    juzCount = catalogState.juzs.size,
                    bookmarkCount = catalogState.bookmarks.size,
                    onTabSelected = { tab ->
                        onAction(QuranUiAction.SelectCatalogTab(tab))
                    }
                )
            }

            // Tab Content
            when (catalogState.activeTab) {
                CatalogTab.SURAH -> {
                    if (catalogState.isCatalogLoading && catalogState.surahs.isEmpty()) {
                        item(key = "catalog_loading") {
                            QuranLoadingSkeleton()
                        }
                    } else if (catalogState.filteredSurahs.isEmpty() && catalogState.searchQuery.isNotBlank()) {
                        item(key = "no_surahs_found") {
                            Text(
                                text = "No Surahs matching \"${catalogState.searchQuery}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp)
                            )
                        }
                    } else {
                        items(
                            items = catalogState.filteredSurahs,
                            key = { it.number }
                        ) { surah ->
                            val isActive = isSplitPane && (surah.number == activeSurahNumber || surah.number == facingSurahNumber)
                            val badgeLabel = if (isSplitPane) {
                                if (surah.number == activeSurahNumber) "Active"
                                else if (surah.number == facingSurahNumber) "Facing"
                                else null
                            } else null

                            SurahListItemCard(
                                surah = surah,
                                isActive = isActive,
                                badgeLabel = badgeLabel,
                                onClick = {
                                    onAction(QuranUiAction.JumpToSurah(surah.number, 1))
                                }
                            )
                        }
                    }
                }
                CatalogTab.JUZ -> {
                    items(
                        items = catalogState.juzs,
                        key = { it.number }
                    ) { juz ->
                        JuzListItemCard(
                            juz = juz,
                            onClick = {
                                onAction(QuranUiAction.JumpToJuz(juz.number))
                            }
                        )
                    }
                }
                CatalogTab.BOOKMARKS -> {
                    if (catalogState.bookmarks.isEmpty()) {
                        item(key = "bookmarks_empty") {
                            BookmarksEmptyState()
                        }
                    } else {
                        items(
                            items = catalogState.bookmarks,
                            key = { it.globalAyahNumber }
                        ) { bookmark ->
                            BookmarkListItemCard(
                                bookmark = bookmark,
                                onClick = {
                                    onAction(QuranUiAction.JumpToSurah(bookmark.surahNumber, bookmark.ayahNumberInSurah))
                                },
                                onRemove = {
                                    onAction(QuranUiAction.RemoveBookmark(bookmark))
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Spacing
            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Quick Jump Scrubber BottomSheet
        QuickJumpBottomSheet(
            isVisible = catalogState.isQuickJumpSheetVisible,
            surahs = catalogState.surahs,
            activeTab = catalogState.activeQuickJumpTab,
            onSelectTab = { onAction(QuranUiAction.SelectQuickJumpTab(it)) },
            onJumpToAyah = { surah, ayah ->
                onAction(QuranUiAction.JumpToSurah(surah, ayah))
            },
            onJumpToPage = { page ->
                onAction(QuranUiAction.JumpToMushafPage(page))
            },
            onJumpToJuz = { juz ->
                onAction(QuranUiAction.JumpToJuz(juz))
            },
            onDismiss = {
                onAction(QuranUiAction.SetQuickJumpSheetVisible(false))
            }
        )
    }
}

@Preview
@Composable
private fun SurahCatalogPanePreview() {
    MeNgajiTheme {
        SurahCatalogPane(
            catalogState = QuranCatalogState(),
            onAction = {}
        )
    }
}

