package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyProfileViewBasic(
    val did: String,
    val handle: String,
    val displayName: String,
    val avatar: String,
    val viewerState: BskyProfileViewerState? = null
)