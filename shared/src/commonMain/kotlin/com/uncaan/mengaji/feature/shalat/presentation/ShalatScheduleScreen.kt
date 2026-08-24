package com.uncaan.mengaji.feature.shalat.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uncaan.mengaji.feature.shalat.presentation.component.ShalatComingSoonBadge
import com.uncaan.mengaji.feature.shalat.presentation.component.ShalatHeroBanner
import com.uncaan.mengaji.feature.shalat.presentation.component.ShalatNotifyCtaButton
import com.uncaan.mengaji.feature.shalat.presentation.component.ShalatRoadmapCard
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stateful entry point composable for the Shalat Schedule feature tab.
 *
 * Injects [ShalatViewModel] via Koin, collects [ShalatUiState] with lifecycle awareness,
 * manages the [SnackbarHostState] for user notifications, and delegates visual rendering
 * to [ShalatScheduleContent].
 *
 * @param modifier Layout modifier applied to the root container.
 * @param viewModel ViewModel orchestrating the Shalat roadmap state and actions.
 * @see ShalatScheduleContent
 * @see ShalatViewModel
 */
@Composable
fun ShalatScheduleScreen(
    modifier: Modifier = Modifier,
    viewModel: ShalatViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        val message = uiState.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.onAction(ShalatUiAction.UserMessageShown)
        }
    }

    ShalatScheduleContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

/**
 * Stateless content composable for the Shalat Schedule "Coming Soon" screen.
 *
 * Renders the Islamic hero banner, header title with coming soon badge, feature teaser
 * roadmap cards, and the opt-in notification CTA button within a vertically scrollable layout.
 *
 * @param uiState Current immutable UI state.
 * @param onAction Action callback dispatched to the ViewModel.
 * @param snackbarHostState Host state controlling the display of feedback Snackbars.
 * @param modifier Layout modifier applied to the root container.
 * @see ShalatUiState
 * @see ShalatUiAction
 */
@Composable
fun ShalatScheduleContent(
    uiState: ShalatUiState,
    onAction: (ShalatUiAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner
            ShalatHeroBanner()

            // Header Title & Coming Soon Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Daily Shalat Schedule",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = false)
                )

                ShalatComingSoonBadge()
            }

            // Introduction Teaser Text
            Text(
                text = "We are actively crafting a comprehensive Islamic prayer schedule companion. Explore our upcoming roadmap features below:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Feature Roadmap Cards
            uiState.roadmapItems.forEach { item ->
                ShalatRoadmapCard(item = item)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Notification Opt-in CTA Button
            ShalatNotifyCtaButton(
                isSubscribed = uiState.isSubscribed,
                onClick = { onAction(ShalatUiAction.NotifyMeClicked) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Feedback Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            snackbar = { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }
        )
    }
}
