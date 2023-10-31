package dev.itswin11.greenland.models.bsky

import dev.itswin11.greenland.models.atproto.AtProtoLabel
import kotlinx.serialization.Serializable

@Serializable
data class BskyProfileView(
    val did: String,
    val handle: String,
    val displayName: String? = null,
    val avatar: String? = null,
    val description: String? = null,
    val indexedAt: String? = null,
    val viewerState: BskyProfileViewerState? = null,
    val labels: List<AtProtoLabel>
)