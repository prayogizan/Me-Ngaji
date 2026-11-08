package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.feature.quran.presentation.model.CatalogTab
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.theme.PrimaryGreen

/**
 * 3-Way Segmented Control for switching between Surah, Juz, and Bookmarks catalog panes.
 *
 * Designed per Figma Screen 01 specs (`node-id=1-49`), providing smooth tab transitions
 * and dynamic bookmark counts.
 *
 * @param activeTab The currently selected [CatalogTab].
 * @param surahCount Total number of Surahs in the catalog (defaults to 114).
 * @param juzCount Total number of Juz in the catalog (defaults to 30).
 * @param bookmarkCount Number of currently saved bookmarks.
 * @param onTabSelected Callback invoked when a tab segment is tapped.
 * @param modifier Layout modifier applied to the segmented container.
 * @see CatalogTab
 */
@Composable
fun SegmentedCatalogTabs(
    activeTab: CatalogTab,
    surahCount: Int = 114,
    juzCount: Int = 30,
    bookmarkCount: Int = 0,
    onTabSelected: (CatalogTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CatalogTabSegment(
                label = "Surah ($surahCount)",
                isSelected = activeTab == CatalogTab.SURAH,
                onClick = { onTabSelected(CatalogTab.SURAH) },
                modifier = Modifier.weight(1f)
            )

            CatalogTabSegment(
                label = "Juz ($juzCount)",
                isSelected = activeTab == CatalogTab.JUZ,
                onClick = { onTabSelected(CatalogTab.JUZ) },
                modifier = Modifier.weight(1f)
            )

            CatalogTabSegment(
                label = "Bookmarks ($bookmarkCount)",
                isSelected = activeTab == CatalogTab.BOOKMARKS,
                onClick = { onTabSelected(CatalogTab.BOOKMARKS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CatalogTabSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen else Color.Transparent,
        animationSpec = tween(durationMillis = 200)
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun SegmentedCatalogTabsPreview() {
    MeNgajiTheme {
        SegmentedCatalogTabs(
            activeTab = CatalogTab.SURAH,
            surahCount = 114,
            juzCount = 30,
            bookmarkCount = 8,
            onTabSelected = {}
        )
    }
}

