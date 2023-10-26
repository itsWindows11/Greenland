package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetSuggestedFeedsInput(val limit: Int = 50, val cursor: String? = null)