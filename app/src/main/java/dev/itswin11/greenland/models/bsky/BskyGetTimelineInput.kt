package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetTimelineInput(val limit: Int = 50, val algorithm: String? = null, val cursor: String? = null)