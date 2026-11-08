package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.theme.PrimaryGreen
import com.uncaan.mengaji.theme.SecondaryGold

/**
 * Direct verse jump action chip displayed when the user types a `surah:ayah` reference pattern (e.g., `2:255`).
 *
 * Provides a 1-tap shortcut to jump directly to the matched verse without scrolling through the catalog.
 *
 * @param surahNumber The parsed 1-indexed Surah number (`1..114`).
 * @param ayahNumber The parsed 1-indexed Ayah number within the Surah.
 * @param onJump Callback invoked when the user taps the chip.
 * @param modifier Layout modifier applied to the chip container.
 */
@Composable
fun DirectAyahJumpChip(
    surahNumber: Int,
    ayahNumber: Int,
    onJump: (surahNumber: Int, ayahNumber: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onJump(surahNumber, ayahNumber) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⚡ Direct Jump:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = SecondaryGold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Go to Surah $surahNumber, Ayah $ayahNumber",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "Open →",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = PrimaryGreen
            )
        }
    }
}

@Preview
@Composable
private fun DirectAyahJumpChipPreview() {
    MeNgajiTheme {
        DirectAyahJumpChip(
            surahNumber = 2,
            ayahNumber = 255,
            onJump = { _, _ -> }
        )
    }
}

