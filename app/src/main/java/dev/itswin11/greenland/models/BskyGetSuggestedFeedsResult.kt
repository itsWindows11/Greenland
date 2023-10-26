package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetSuggestedFeedsResult(val feeds: List<BskyFeedGeneratorView>, val cursor: String? = null)