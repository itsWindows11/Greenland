package dev.itswin11.greenland.models.bsky

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class BskyPostRecord(
    val text: String,
    @SerializedName("\$type")
    val type: String? = null,
    // val embed: Any
    val langs: List<String>? = null,
    val createdAt: String
)