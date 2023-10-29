package dev.itswin11.greenland.models.bsky

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class BskyPostViewerState(val repost: String, val like: String)