package dev.itswin11.greenland.main

import dev.itswin11.greenland.models.AtProtoCreateSessionResult
import dev.itswin11.greenland.models.BskyGetFeedGeneratorResult
import dev.itswin11.greenland.models.BskyGetFeedGeneratorsResult
import dev.itswin11.greenland.models.BskyGetFeedInput
import dev.itswin11.greenland.models.BskyGetFeedResult
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.models.BskyPreferencesModel

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
    suspend fun getHomeTimeline(server: String, input: BskyGetTimelineInput): BskyGetFeedResult

    /**
     * Gets the Bluesky preferences.
     */
    suspend fun getPreferences(server: String): BskyPreferencesModel

    /**
     * Gets info about multiple Bluesky feed generators.
     */
    suspend fun getFeedGenerators(server: String, feedUris: Iterable<String>): BskyGetFeedGeneratorsResult

    /**
     * Gets info about a Bluesky feed generator.
     */
    suspend fun getFeedGenerator(server: String, feedUri: String): BskyGetFeedGeneratorResult

    /**
     * Composes a feed from a Bluesky feed generator.
     */
    suspend fun getFeed(server: String, input: BskyGetFeedInput): BskyGetFeedResult
}