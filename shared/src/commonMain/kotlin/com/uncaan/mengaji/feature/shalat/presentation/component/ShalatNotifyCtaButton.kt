package com.uncaan.mengaji.feature.shalat.presentation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mengaji.shared.generated.resources.Res
import mengaji.shared.generated.resources.ic_adhan_bell
import mengaji.shared.generated.resources.ic_notification_active
import org.jetbrains.compose.resources.painterResource

/**
 * Call-To-Action button allowing the user to opt-in for prayer time launch notifications.
 *
 * Dynamically switches appearance and label depending on the [isSubscribed] state.
 *
 * @param isSubscribed Whether the user has already opted in.
 * @param onClick Callback invoked when the button is clicked.
 * @param modifier The layout modifier applied to the button.
 */
@Composable
fun ShalatNotifyCtaButton(
    isSubscribed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSubscribed) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.primary
            },
            contentColor = if (isSubscribed) {
                MaterialTheme.colorScheme.onSecondary
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Icon(
            painter = painterResource(
                if (isSubscribed) Res.drawable.ic_notification_active else Res.drawable.ic_adhan_bell
            ),
            contentDescription = if (isSubscribed) "Notification Registered" else "Notify Me",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = if (isSubscribed) "Notification Registered" else "Notify Me When Ready",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
