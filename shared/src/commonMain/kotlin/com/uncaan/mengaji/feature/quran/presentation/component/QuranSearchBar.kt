package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mengaji.shared.generated.resources.Res
import mengaji.shared.generated.resources.ic_close
import mengaji.shared.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource

/**
 * Styled text input field for submitting Ayah search queries.
 *
 * Supports keyboard IME search actions, leading search icon button, and trailing clear button.
 *
 * @param query Current text query in the input field.
 * @param onQueryChange Callback triggered on text modification.
 * @param onSearch Callback triggered when the search button or keyboard IME action is clicked.
 * @param onClear Callback triggered when the clear trailing button is clicked.
 * @param modifier Layout modifier applied to the text field.
 */
@Composable
fun QuranSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search Ayah (e.g., 2:255 or 262)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    onSearch(query)
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = "Search Ayah",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onSearch(query)
            }
        )
    )
}
