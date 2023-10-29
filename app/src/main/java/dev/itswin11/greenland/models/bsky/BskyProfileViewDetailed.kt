package dev.itswin11.greenland.models.bsky

import androidx.compose.runtime.Immutable
import dev.itswin11.greenland.models.atproto.AtProtoLabel
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class BskyProfileViewDetailed(
    val did: String,
    val handle: String,
    val displayName: String? = null,
    val avatar: String? = null,
    val banner: String? = null,
    val followersCount: Int? = null,
    val followsCount: Int? = null,
    val postsCount: Int? = null,
    val description: String? = null,
    val indexedAt: String? = null,
    val viewerState: BskyProfileViewerState? = null,
    val labels: List<AtProtoLabel>
)