package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.theme.PrimaryGreen
import com.uncaan.mengaji.theme.getQuranArabicTextStyle

/**
 * List item card displaying a single Surah's ordinal number, titles, and revelation metadata.
 *
 * Designed per Figma Screen 01 specs (`node-id=1-55`), featuring an 8-point badge,
 * English name with revelation summary, and an authentic RTL Arabic calligraphy title.
 *
 * @param surah The [SurahSummary] domain model to render.
 * @param onClick Callback triggered when the card is tapped.
 * @param modifier Layout modifier applied to the card container.
 * @see SurahSummary
 */
@Composable
fun SurahListItemCard(
    surah: SurahSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Surah Number Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = surah.number.toString(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Center: English Title & Revelation Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.englishName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${surah.englishNameTranslation} • ${surah.numberOfAyahs} Ayahs • ${surah.revelationType.name}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: Arabic Calligraphy Title
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = surah.name,
                    style = getQuranArabicTextStyle().copy(
                        fontSize = 20.sp,
                        lineHeight = 28.sp
                    ),
                    color = PrimaryGreen,
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

@Preview
@Composable
private fun SurahListItemCardPreview() {
    MeNgajiTheme {
        SurahListItemCard(
            surah = SurahSummary(
                number = 1,
                name = "ٱلْفَاتِحَة",
                englishName = "Al-Fatihah",
                englishNameTranslation = "The Opening",
                numberOfAyahs = 7,
                revelationType = RevelationType.MECCAN
            ),
            onClick = {}
        )
    }
}

