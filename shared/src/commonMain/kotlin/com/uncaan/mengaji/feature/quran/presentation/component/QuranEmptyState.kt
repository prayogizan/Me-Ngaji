package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class QuickAyahSuggestion(
    val title: String,
    val reference: String
)

private val SUGGESTIONS = listOf(
    QuickAyahSuggestion("Al-Fatihah 1:1", "1:1"),
    QuickAyahSuggestion("Ayat Al-Kursi 2:255", "2:255"),
    QuickAyahSuggestion("Al-Ikhlas 112:1", "112:1"),
    QuickAyahSuggestion("Al-Falaq 113:1", "113:1"),
    QuickAyahSuggestion("An-Nas 114:1", "114:1"),
    QuickAyahSuggestion("Al-Mulk 67:1", "67:1")
)

/**
 * Empty state card displayed on initial screen launch prior to any search queries.
 *
 * Offers quick-suggestion chips for popular verses (e.g. Al-Fatihah, Ayat Al-Kursi, Al-Ikhlas)
 * to facilitate one-tap verse discovery.
 *
 * @param onSelectSuggestion Callback triggered when a quick-suggestion chip is tapped.
 * @param modifier Layout modifier applied to the root card container.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuranEmptyState(
    onSelectSuggestion: (String) -> Unit,
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📖 Explore the Holy Quran",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter a Surah and Ayah reference above (e.g., 2:255) or select one of the popular Ayahs below to start reading.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Popular Ayahs",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SUGGESTIONS.forEach { suggestion ->
                    SuggestionChip(
                        onClick = { onSelectSuggestion(suggestion.reference) },
                        label = { Text(text = suggestion.title) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}
