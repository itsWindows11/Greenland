package dev.itswin11.greenland.main

import dev.itswin11.greenland.models.AtProtoCreateSessionResult
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.models.BskyGetTimelineResult

interface IAtProtoClient {
    /**
     * Creates a session with the AT Protocol server.
     */
    suspend fun createSession(server: String, identifier: String, password: String): AtProtoCreateSessionResult

    /**
     * Automatically renews the AT Protocol session if needed.
     */
    suspend fun refreshSessionIfNeeded(server: String, checkForExpiration: Boolean)

    /**
     * Gets the Bluesky home feed.
     */
    suspend fun getHomeTimeline(server: String, input: BskyGetTimelineInput): BskyGetTimelineResult
}