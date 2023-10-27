package dev.itswin11.greenland.main

import dev.itswin11.greenland.models.AtProtoCreateSessionResult
import dev.itswin11.greenland.models.BskyGetFeedGeneratorResult
import dev.itswin11.greenland.models.BskyGetFeedGeneratorsResult
import dev.itswin11.greenland.models.BskyGetFeedInput
import dev.itswin11.greenland.models.BskyGetFeedResult
import dev.itswin11.greenland.models.BskyGetSuggestedFeedsInput
import dev.itswin11.greenland.models.BskyGetSuggestedFeedsResult
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.models.BskyPreferencesModel

interface IAtProtoClient {
    /**
     * Creates a session with the AT Protocol server.
     */
    suspend fun createSession(identifier: String, password: String): AtProtoCreateSessionResult

    /**
     * Automatically renews the AT Protocol session if needed.
     */
    suspend fun refreshSessionIfNeeded(checkForExpiration: Boolean)

    /**
     * Gets the Bluesky home feed.
     */
    suspend fun getHomeTimeline(input: BskyGetTimelineInput): BskyGetFeedResult

    /**
     * Gets the Bluesky preferences.
     */
    suspend fun getPreferences(): BskyPreferencesModel

    /**
     * Gets info about multiple Bluesky feed generators.
     */
    suspend fun getFeedGenerators(feedUris: Iterable<String>): BskyGetFeedGeneratorsResult

    /**
     * Gets info about a Bluesky feed generator.
     */
    suspend fun getFeedGenerator(feedUri: String): BskyGetFeedGeneratorResult

    /**
     * Composes a feed from a Bluesky feed generator.
     */
    suspend fun getFeed(input: BskyGetFeedInput): BskyGetFeedResult

    /**
     * Gets the unread notification count for the current Bluesky user.
     */
    suspend fun getUnreadNotificationsCount(): Int

    /**
     * Gets suggested feeds for the current Bluesky user.
     */
    suspend fun getSuggestedFeeds(input: BskyGetSuggestedFeedsInput): BskyGetSuggestedFeedsResult
}