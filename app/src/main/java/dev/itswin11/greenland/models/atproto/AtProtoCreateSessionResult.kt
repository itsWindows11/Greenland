package dev.itswin11.greenland.models.atproto

import kotlinx.serialization.Serializable

@Serializable
data class AtProtoCreateSessionResult(
    val did: String,
    val handle: String,
    val email: String? = null,
    val accessJwt: String,
    val refreshJwt: String
)
