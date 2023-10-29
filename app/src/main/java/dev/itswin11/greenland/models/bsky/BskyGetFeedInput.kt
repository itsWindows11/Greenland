package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetFeedInput(val feed: String, val cursor: String? = null, val limit: Int = 50)