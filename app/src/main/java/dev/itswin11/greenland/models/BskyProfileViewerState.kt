package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyProfileViewerState(
    val muted: Boolean,
    val blockedBy: Boolean,
    val blocking: String,
    val following: String,
    val followedBy: String
)