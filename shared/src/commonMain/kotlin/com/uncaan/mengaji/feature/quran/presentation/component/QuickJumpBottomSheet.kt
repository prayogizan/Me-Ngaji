package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.feature.quran.domain.model.JuzCatalogData
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.presentation.model.QuickJumpTab
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.theme.PrimaryGreen
import com.uncaan.mengaji.theme.SecondaryGold

/**
 * Modal BottomSheet scrubber facilitating rapid jump navigation across Surahs, Ayahs,
 * Medina Mushaf pages (`1..604`), and the 30 Juz parts.
 *
 * @param isVisible Whether the bottom sheet is currently open.
 * @param surahs Complete 114-Surah catalog list used to populate Surah and verse selectors.
 * @param activeTab The currently selected mode tab ([QuickJumpTab]).
 * @param onSelectTab Callback triggered when switching modes within the sheet.
 * @param onJumpToAyah Callback invoked when confirming a Surah and Ayah target.
 * @param onJumpToPage Callback invoked when confirming a Medina Mushaf page target.
 * @param onJumpToJuz Callback invoked when tapping a Juz grid cell.
 * @param onDismiss Callback invoked when the user dismisses the bottom sheet.
 * @param modifier Layout modifier applied to the bottom sheet content.
 * @see QuickJumpTab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickJumpBottomSheet(
    isVisible: Boolean,
    surahs: List<SurahSummary>,
    activeTab: QuickJumpTab,
    onSelectTab: (QuickJumpTab) -> Unit,
    onJumpToAyah: (surahNumber: Int, ayahNumber: Int) -> Unit,
    onJumpToPage: (pageNumber: Int) -> Unit,
    onJumpToJuz: (juzNumber: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ Quick Jump Scrubber",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-mode Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickJumpTabButton(
                    label = "Surah & Ayah",
                    isSelected = activeTab == QuickJumpTab.SURAH_AYAH,
                    onClick = { onSelectTab(QuickJumpTab.SURAH_AYAH) },
                    modifier = Modifier.weight(1f)
                )
                QuickJumpTabButton(
                    label = "Mushaf Page",
                    isSelected = activeTab == QuickJumpTab.MUSHAF_PAGE,
                    onClick = { onSelectTab(QuickJumpTab.MUSHAF_PAGE) },
                    modifier = Modifier.weight(1f)
                )
                QuickJumpTabButton(
                    label = "Juz Grid",
                    isSelected = activeTab == QuickJumpTab.JUZ_GRID,
                    onClick = { onSelectTab(QuickJumpTab.JUZ_GRID) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Tab Content
            when (activeTab) {
                QuickJumpTab.SURAH_AYAH -> {
                    SurahAyahPickerContent(
                        surahs = surahs,
                        onConfirm = onJumpToAyah
                    )
                }
                QuickJumpTab.MUSHAF_PAGE -> {
                    MushafPageScrubberContent(
                        onConfirm = onJumpToPage
                    )
                }
                QuickJumpTab.JUZ_GRID -> {
                    JuzGridContent(
                        onSelectJuz = onJumpToJuz
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickJumpTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PrimaryGreen else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurahAyahPickerContent(
    surahs: List<SurahSummary>,
    onConfirm: (surahNumber: Int, ayahNumber: Int) -> Unit
) {
    var selectedSurahNumber by remember { mutableIntStateOf(surahs.firstOrNull()?.number ?: 1) }
    val currentSurah = surahs.find { it.number == selectedSurahNumber } ?: surahs.firstOrNull()
    val maxAyahs = currentSurah?.numberOfAyahs ?: 286

    var selectedAyahNumber by remember { mutableIntStateOf(1) }
    var ayahInputText by remember { mutableStateOf("1") }
    var isSurahDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Surah",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = isSurahDropdownExpanded,
            onExpandedChange = { isSurahDropdownExpanded = !isSurahDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = currentSurah?.let { "${it.number}. ${it.englishName} (${it.name})" } ?: "Select Surah",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSurahDropdownExpanded) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
            )

            ExposedDropdownMenu(
                expanded = isSurahDropdownExpanded,
                onDismissRequest = { isSurahDropdownExpanded = false }
            ) {
                surahs.forEach { s ->
                    DropdownMenuItem(
                        text = { Text("${s.number}. ${s.englishName} • ${s.name}") },
                        onClick = {
                            selectedSurahNumber = s.number
                            selectedAyahNumber = 1
                            ayahInputText = "1"
                            isSurahDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Ayah Number (1 to $maxAyahs)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = ayahInputText,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }
                ayahInputText = digits
                val num = digits.toIntOrNull()
                if (num != null && num in 1..maxAyahs) {
                    selectedAyahNumber = num
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { onConfirm(selectedSurahNumber, selectedAyahNumber) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Jump to Surah $selectedSurahNumber, Ayah $selectedAyahNumber",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun MushafPageScrubberContent(
    onConfirm: (pageNumber: Int) -> Unit
) {
    var pageSliderValue by remember { mutableFloatStateOf(1f) }
    val currentPage = pageSliderValue.toInt().coerceIn(1, 604)
    val correspondingJuz = remember(currentPage) { JuzCatalogData.getJuzForPage(currentPage) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Medina Mushaf Page",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SecondaryGold
            ) {
                Text(
                    text = "Page $currentPage / 604",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = Color(0xFF121413),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            value = pageSliderValue,
            onValueChange = { pageSliderValue = it },
            valueRange = 1f..604f,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryGreen,
                activeTrackColor = PrimaryGreen,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Resides in Juz ${correspondingJuz.number} (${correspondingJuz.nameArabic})",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { onConfirm(currentPage) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Open Page $currentPage",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun JuzGridContent(
    onSelectJuz: (juzNumber: Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Juz to begin reciting:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            items(JuzCatalogData.allJuz, key = { it.number }) { juz ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clickable { onSelectJuz(juz.number) }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Juz",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = juz.number.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = PrimaryGreen
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun QuickJumpBottomSheetPreview() {
    MeNgajiTheme {
        QuickJumpBottomSheet(
            isVisible = true,
            surahs = emptyList(),
            activeTab = QuickJumpTab.SURAH_AYAH,
            onSelectTab = {},
            onJumpToAyah = { _, _ -> },
            onJumpToPage = {},
            onJumpToJuz = {},
            onDismiss = {}
        )
    }
}

