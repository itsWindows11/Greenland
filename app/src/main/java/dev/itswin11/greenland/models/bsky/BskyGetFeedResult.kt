package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetFeedResult(val feed: List<BskyFeedViewPost>, val cursor: String?)