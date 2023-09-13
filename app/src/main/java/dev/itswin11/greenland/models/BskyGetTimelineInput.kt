package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetTimelineInput(val limit: Int = 50, val algorithm: String? = null, val cursor: String? = null)