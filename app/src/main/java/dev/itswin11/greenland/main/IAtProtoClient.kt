package dev.itswin11.greenland.main

import dev.itswin11.greenland.models.atproto.AtProtoCreateSessionResult
import dev.itswin11.greenland.models.bsky.BskyGetFeedGeneratorResult
import dev.itswin11.greenland.models.bsky.BskyGetFeedGeneratorsResult
import dev.itswin11.greenland.models.bsky.BskyGetFeedInput
import dev.itswin11.greenland.models.bsky.BskyGetFeedResult
import dev.itswin11.greenland.models.bsky.BskyGetSuggestedFeedsInput
import dev.itswin11.greenland.models.bsky.BskyGetSuggestedFeedsResult
import dev.itswin11.greenland.models.bsky.BskyGetSuggestedFollowsByActorResult
import dev.itswin11.greenland.models.bsky.BskyGetTimelineInput
import dev.itswin11.greenland.models.bsky.BskyPreferencesModel

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

    /**
     * Gets suggested follows for a given Bluesky actor.
     */
    suspend fun getSuggestedFollowsByActor(actor: String): BskyGetSuggestedFollowsByActorResult
}