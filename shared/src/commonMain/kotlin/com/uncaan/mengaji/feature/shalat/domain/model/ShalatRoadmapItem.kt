package com.uncaan.mengaji.feature.shalat.domain.model

import org.jetbrains.compose.resources.DrawableResource

/**
 * Represents an upcoming feature item displayed in the Shalat Schedule roadmap teaser.
 *
 * This is a pure domain entity used to model roadmap capabilities shown on the "Coming Soon"
 * landing screen.
 *
 * @property id Unique identifier for the roadmap feature.
 * @property title The primary display title of the upcoming feature.
 * @property description Detailed explanation of the capability and how it benefits the user.
 * @property icon The [DrawableResource] representing the feature's visual icon.
 * @property highlightBadge Optional tag or badge text highlighting a specific aspect (e.g., "5 Daily Prayers").
 */
data class ShalatRoadmapItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: DrawableResource,
    val highlightBadge: String? = null
)
