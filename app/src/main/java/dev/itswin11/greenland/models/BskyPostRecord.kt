package dev.itswin11.greenland.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class BskyPostRecord(
    val text: String,
    @SerializedName("\$type")
    val type: String? = null,
    // val embed: Any
    val langs: List<String>,
    val createdAt: String
)