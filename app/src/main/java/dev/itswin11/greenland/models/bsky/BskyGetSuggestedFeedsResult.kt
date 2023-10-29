package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetSuggestedFeedsResult(val feeds: List<BskyFeedGeneratorView>, val cursor: String? = null)