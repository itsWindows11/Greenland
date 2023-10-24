package dev.itswin11.greenland.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class BskyFeedGeneratorView(
    val uri: String,
    val cid: String,
    @SerializedName("did")
    val creatorDid: String,
    // val creator: Any
    val displayName: String,
    val description: String? = null,
    val avatar: String? = null,
    val likeCount: Int = 0,
    val indexedAt: String
)