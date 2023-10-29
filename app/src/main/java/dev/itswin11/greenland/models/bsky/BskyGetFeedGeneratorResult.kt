package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetFeedGeneratorResult(
    val view: BskyFeedGeneratorView,
    val isOnline: Boolean,
    val isValid: Boolean
)