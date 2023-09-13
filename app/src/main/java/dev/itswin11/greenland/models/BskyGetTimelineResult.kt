package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetTimelineResult (val feed: List<BskyFeedViewPost>, val cursor: String?)