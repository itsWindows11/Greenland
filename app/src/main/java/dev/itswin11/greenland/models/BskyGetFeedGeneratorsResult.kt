package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetFeedGeneratorsResult(val feeds: List<BskyFeedGeneratorView>)