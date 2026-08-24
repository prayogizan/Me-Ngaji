package com.uncaan.mengaji.feature.shalat.presentation

import com.uncaan.mengaji.feature.shalat.domain.model.ShalatRoadmapItem

/**
 * Immutable state model representing the current state of the Shalat Schedule screen.
 *
 * Emitted by [ShalatViewModel] and observed by composables to render the roadmap
 * teaser cards and manage user opt-in status.
 *
 * @property isSubscribed Whether the user has opted in to receive launch notifications.
 * @property roadmapItems The list of [ShalatRoadmapItem] feature teasers to display.
 * @property userMessage An optional transient message string to be displayed via a Snackbar.
 * @see ShalatViewModel
 * @see ShalatUiAction
 */
data class ShalatUiState(
    val isSubscribed: Boolean = false,
    val roadmapItems: List<ShalatRoadmapItem> = emptyList(),
    val userMessage: String? = null
)

/**
 * Sealed hierarchy of user-initiated actions on the Shalat Schedule screen.
 *
 * Actions are dispatched from UI composables to [ShalatViewModel.onAction] for processing.
 *
 * @see ShalatViewModel.onAction
 */
sealed interface ShalatUiAction {

    /**
     * User tapped the "Notify Me" CTA button to opt in for feature launch notifications.
     */
    data object NotifyMeClicked : ShalatUiAction

    /**
     * Dispatched after a transient [ShalatUiState.userMessage] has been displayed to the user.
     */
    data object UserMessageShown : ShalatUiAction
}
