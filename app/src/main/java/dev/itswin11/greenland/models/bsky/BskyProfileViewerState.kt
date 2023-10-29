package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyProfileViewerState(
    val muted: Boolean,
    val blockedBy: Boolean,
    val blocking: String? = null,
    // TODO: blockingByList
    val following: String? = null,
    val followedBy: String? = null,
)