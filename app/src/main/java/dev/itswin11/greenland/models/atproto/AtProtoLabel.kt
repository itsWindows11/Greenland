package dev.itswin11.greenland.models.atproto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AtProtoLabel(
    val src: String,
    val uri: String,
    val cid: String? = null,
    @SerializedName("val") val value: String,
    val neg: Boolean,
    val cts: String
)