package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class AtProtoRefreshSessionInfo(val accessJwt: String, val refreshJwt: String, val handle: String, val did: String)