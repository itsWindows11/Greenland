package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyReplyRef(val root: BskyPost, val reply: BskyPost? = null)