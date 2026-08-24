package com.uncaan.mengaji.feature.shalat.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uncaan.mengaji.theme.SecondaryGold
import com.uncaan.mengaji.theme.SecondaryGoldLight

/**
 * Status badge displaying the "Coming Soon" tag.
 *
 * Styled as an Islamic gold-tinted pill surface with rounded borders to indicate upcoming
 * feature readiness.
 *
 * @param modifier The layout modifier applied to the badge.
 */
@Composable
fun ShalatComingSoonBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SecondaryGoldLight.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, SecondaryGold),
        modifier = modifier
    ) {
        Text(
            text = "Coming Soon",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
