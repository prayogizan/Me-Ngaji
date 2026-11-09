package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.presentation.QuranUiAction
import com.uncaan.mengaji.theme.PrimaryGreen

/**
 * Contextual bottom sheet for verse-level actions matching Figma `AyahActionBottomSheet` (node `1:179`).
 *
 * Provides actions for:
 * - Starting continuous recitation from this specific verse (`Play from Ayah`).
 * - Infinite single-verse repetition for memorization / Hifz revision (`Loop Ayah`).
 * - Recording manual recitation progress checkpoint (`Mark as Last Read`).
 * - Toggling personal bookmark status (`Bookmark`).
 * - Copying formatted Arabic and translation text to system clipboard (`Share / Copy`).
 *
 * @param ayah The target [Ayah] domain entity being inspected.
 * @param onAction Action dispatcher for executing the chosen operation.
 * @param onDismiss Callback invoked when the sheet is dismissed or dragged down.
 * @param modifier Layout modifier applied to the bottom sheet content container.
 * @see Ayah
 * @see QuranUiAction
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahActionBottomSheet(
    ayah: Ayah,
    onAction: (QuranUiAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Row: Surah & Ayah Reference + Page/Juz Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${ayah.surahName} • Ayah ${ayah.surahNumber}:${ayah.numberInSurah}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryGreen.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Page ${ayah.page} • Juz ${ayah.juz}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Translation Excerpt Snippet
            Text(
                text = "\"${ayah.translation}\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(14.dp))

            // Primary Recitation Action Buttons: Play from Here & Loop Ayah (Hifz)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Play from Ayah
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onAction(QuranUiAction.PlayFromAyah(ayah))
                            onDismiss()
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryGreen
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "▶ Play from Here",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                // Loop Ayah (Hifz)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onAction(QuranUiAction.LoopAyah(ayah))
                            onDismiss()
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔁 Loop (Hifz)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Quick Actions: Last Read, Bookmark, Copy / Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mark Last Read
                AyahActionButton(
                    icon = "📌",
                    label = "Last Read",
                    onClick = {
                        onAction(QuranUiAction.SaveManualLastRead(ayah))
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )

                // Bookmark
                AyahActionButton(
                    icon = "★",
                    label = "Bookmark",
                    onClick = {
                        onAction(QuranUiAction.ToggleBookmarkForAyah(ayah))
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )

                // Copy / Share
                AyahActionButton(
                    icon = "📤",
                    label = "Copy",
                    onClick = {
                        val formatted = "${ayah.text}\n\n${ayah.translation}\n(${ayah.surahName} ${ayah.surahNumber}:${ayah.numberInSurah})"
                        clipboardManager.setText(AnnotatedString(formatted))
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AyahActionButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

