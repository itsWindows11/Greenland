package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyFeedViewPost(val post: BskyPost, val reply: BskyReplyRef? = null)