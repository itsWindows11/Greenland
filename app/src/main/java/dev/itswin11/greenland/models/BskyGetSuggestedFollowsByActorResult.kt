package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetSuggestedFollowsByActorResult(val suggestions: List<BskyProfileView>)