package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets

/**
 * Horizontally scrollable row containing FilterChips for choosing translation and recitation editions.
 *
 * @param selectedTranslation The identifier of the currently active translation preset.
 * @param selectedRecitation The identifier of the currently active recitation preset.
 * @param onSelectTranslation Callback triggered when a translation chip is clicked.
 * @param onSelectRecitation Callback triggered when a recitation chip is clicked.
 * @param modifier Layout modifier applied to the root column container.
 * @see QuranEditionPresets
 */
@Composable
fun EditionSelectorRow(
    selectedTranslation: String,
    selectedRecitation: String,
    onSelectTranslation: (String) -> Unit,
    onSelectRecitation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Translation Edition",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuranEditionPresets.TRANSLATIONS.forEach { option ->
                FilterChip(
                    selected = selectedTranslation == option.identifier,
                    onClick = { onSelectTranslation(option.identifier) },
                    label = { Text(text = option.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Recitation Reciter",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuranEditionPresets.RECITATIONS.forEach { option ->
                FilterChip(
                    selected = selectedRecitation == option.identifier,
                    onClick = { onSelectRecitation(option.identifier) },
                    label = { Text(text = option.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }
        }
    }
}
