package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyReplyRef(val root: BskyPost, val parent: BskyPost? = null)