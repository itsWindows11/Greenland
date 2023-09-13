package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyPostViewerState(val repost: String, val like: String)