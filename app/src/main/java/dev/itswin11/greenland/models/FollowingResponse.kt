package dev.itswin11.greenland.models

import app.bsky.graph.GetFollowersResponse
import app.bsky.graph.GetFollowsResponse

data class FollowingResponse(
    val subject: Profile,
    val cursor: String? = null,
    val follows: List<Profile>,
)

fun GetFollowsResponse.toFollowingResponse() = FollowingResponse(
    subject = subject.toProfile(),
    cursor = cursor,
    follows = follows.map { it.toProfile() }
)

fun GetFollowersResponse.toFollowingResponse() = FollowingResponse(
    subject = subject.toProfile(),
    cursor = cursor,
    follows = followers.map { it.toProfile() }
)