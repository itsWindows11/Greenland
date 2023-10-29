package dev.itswin11.greenland.models.atproto

import kotlinx.serialization.Serializable

@Serializable
data class AtProtoSessionCredentials(val identifier: String, val password: String)