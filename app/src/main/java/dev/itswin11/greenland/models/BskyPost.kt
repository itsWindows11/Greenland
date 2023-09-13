package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyPost(
    val uri: String,
    val cid: String,
    val author: BskyProfileViewBasic,
    val replyCount: Int,
    val repostCount: Int,
    val likeCount: Int,
    val indexedAt: String,
    val viewerState: BskyPostViewerState? = null
)