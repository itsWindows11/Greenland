package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetFeedGeneratorsResult(val feeds: List<BskyFeedGeneratorView>)