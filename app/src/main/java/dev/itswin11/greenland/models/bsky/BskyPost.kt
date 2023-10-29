package dev.itswin11.greenland.models.bsky

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class BskyPost(
    val uri: String,
    val cid: String,
    val author: BskyProfileViewBasic,
    val record: BskyPostRecord,
    val replyCount: Int,
    val repostCount: Int,
    val likeCount: Int,
    val indexedAt: String,
    val viewerState: BskyPostViewerState? = null
)