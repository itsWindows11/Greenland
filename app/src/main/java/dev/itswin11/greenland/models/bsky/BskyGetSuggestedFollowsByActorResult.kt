package dev.itswin11.greenland.models.bsky

import kotlinx.serialization.Serializable

@Serializable
data class BskyGetSuggestedFollowsByActorResult(val suggestions: List<BskyProfileView>)