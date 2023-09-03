package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class AtProtoSessionCredentials(val identifier: String, val password: String)