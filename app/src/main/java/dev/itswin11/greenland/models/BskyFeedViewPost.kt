package dev.itswin11.greenland.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class BskyFeedViewPost(val post: BskyPost, val reply: BskyReplyRef? = null)