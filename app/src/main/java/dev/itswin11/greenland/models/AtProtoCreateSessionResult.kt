package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class AtProtoCreateSessionResult(
    val did: String,
    val handle: String,
    val email: String,
    val accessJwt: String,
    val refreshJwt: String
)
