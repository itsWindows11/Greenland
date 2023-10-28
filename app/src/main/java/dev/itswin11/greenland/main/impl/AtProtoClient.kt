package dev.itswin11.greenland.main.impl

import dev.itswin11.greenland.App
import dev.itswin11.greenland.authDataStore
import dev.itswin11.greenland.main.IAtProtoClient
import dev.itswin11.greenland.models.AtProtoCreateSessionResult
import dev.itswin11.greenland.models.AtProtoSessionCredentials
import dev.itswin11.greenland.models.BskyGetFeedGeneratorResult
import dev.itswin11.greenland.models.BskyGetFeedGeneratorsResult
import dev.itswin11.greenland.models.BskyGetFeedInput
import dev.itswin11.greenland.models.BskyGetFeedResult
import dev.itswin11.greenland.models.BskyGetSuggestedFeedsInput
import dev.itswin11.greenland.models.BskyGetSuggestedFeedsResult
import dev.itswin11.greenland.models.BskyGetSuggestedFollowsByActorResult
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.models.BskyNotificationCount
import dev.itswin11.greenland.models.BskyPreferencesModel
import dev.itswin11.greenland.protobuf.AuthInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.Calendar

class AtProtoClient(private val server: String) : IAtProtoClient {
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(5)
        }

        install(ContentEncoding) {
            gzip()
            deflate()
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 20000
        }
    }

    override suspend fun createSession(identifier: String, password: String): AtProtoCreateSessionResult {
        val response = httpClient.post("https://$server/xrpc/com.atproto.server.createSession") {
            contentType(ContentType.Application.Json)
            setBody(AtProtoSessionCredentials(identifier, password))
        }

        return response.body()
    }

    override suspend fun refreshSessionIfNeeded(checkForExpiration: Boolean) {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()

        val accessInfo = App.instance.tokenService.getCurrentAccessTokenInfo(currentAccountIndex)
        val info = App.instance.tokenService.getCurrentRefreshTokenInfo(currentAccountIndex)

        val did = App.instance.tokenService.getCurrentDid(currentAccountIndex)
        val handle = App.instance.tokenService.getCurrentHandle(currentAccountIndex)

        if (checkForExpiration && ((accessInfo != null
                    && accessInfo.expiresAt != null
                    && accessInfo.expiresAt!!.time > Calendar.getInstance().time.time)
            || handle == null
            || did == null)) {
            // We do not need to renew the session.
            // And if handle and did are null, we do not
            // need to refresh the session either since
            // we cannot pass the necessary info.
            return
        }

        val result = httpClient.post("https://$server/xrpc/com.atproto.server.refreshSession") {
            contentType(ContentType.Application.Json)

            headers {
                append(HttpHeaders.Authorization, "Bearer ${info.toString()}")
            }
        }

        val response = result.body<AtProtoCreateSessionResult>()

        App.instance.authDataStore.updateData {
            val builder = it.toBuilder()

            val authInfoBuilder = AuthInfo.newBuilder()

            authInfoBuilder.accessJwt = response.accessJwt
            authInfoBuilder.refreshJwt = response.refreshJwt
            authInfoBuilder.did = response.did
            authInfoBuilder.handle = response.handle
            authInfoBuilder.signedIn = true

            builder.signedIn = true

            builder.removeAuthInfo(currentAccountIndex)
            builder.addAuthInfo(currentAccountIndex, authInfoBuilder.build())

            return@updateData builder.build()
        }

        val accessInfo2 = App.instance.tokenService.getCurrentAccessTokenInfo(currentAccountIndex)
        accessInfo2.hashCode()
    }

    override suspend fun getHomeTimeline(input: BskyGetTimelineInput): BskyGetFeedResult {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.feed.getTimeline") {
            parameter("limit", input.limit)

            if (input.cursor != null) {
                parameter("cursor", input.cursor)
            }

            if (input.algorithm != null) {
                parameter("algorithm", input.algorithm)
            }

            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        if (response.status.value == 400 || response.status.value == 401) {
            // In this case we need to refresh the session ASAP.
            // Repeat the method call until we get a successful
            // response.
            refreshSessionIfNeeded(false)
            return getHomeTimeline(input)
        }

        return response.body()
    }

    override suspend fun getPreferences(): BskyPreferencesModel {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.actor.getPreferences") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        if (response.status.value == 400 || response.status.value == 401) {
            // In this case we need to refresh the session ASAP.
            // Repeat the method call until we get a successful
            // response.
            refreshSessionIfNeeded(false)
            return getPreferences()
        }

        return BskyPreferencesModel(response.body())
    }

    override suspend fun getFeedGenerators(feedUris: Iterable<String>): BskyGetFeedGeneratorsResult {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.feed.getFeedGenerators") {
            contentType(ContentType.Application.Json)
            setBody(feedUris)

            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        if (response.status.value == 400 || response.status.value == 401) {
            // In this case we need to refresh the session ASAP.
            // Repeat the method call until we get a successful
            // response.
            refreshSessionIfNeeded(false)
            return getFeedGenerators(feedUris)
        }

        return response.body()
    }

    override suspend fun getFeedGenerator(feedUri: String): BskyGetFeedGeneratorResult {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.feed.getFeedGenerator") {
            contentType(ContentType.Application.Json)
            setBody(feedUri)

            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        if (response.status.value == 400 || response.status.value == 401) {
            // In this case we need to refresh the session ASAP.
            // Repeat the method call until we get a successful
            // response.
            refreshSessionIfNeeded(false)
            return getFeedGenerator(feedUri)
        }

        return response.body()
    }

    override suspend fun getFeed(input: BskyGetFeedInput): BskyGetFeedResult {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.feed.getFeed") {
            parameter("limit", input.limit)
            parameter("feed", input.feed)

            if (input.cursor != null) {
                parameter("cursor", input.cursor)
            }

            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        if (response.status.value == 400 || response.status.value == 401) {
            // In this case we need to refresh the session ASAP.
            // Repeat the method call until we get a successful
            // response.
            refreshSessionIfNeeded(false)
            return getFeed(input)
        }

        return response.body()
    }

    override suspend fun getUnreadNotificationsCount(): Int {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.notification.getUnreadCount") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        if (response.status.value == 400 || response.status.value == 401) {
            // In this case we need to refresh the session ASAP.
            // Repeat the method call until we get a successful
            // response.
            refreshSessionIfNeeded(false)
            return getUnreadNotificationsCount()
        }

        return response.body<BskyNotificationCount>().count
    }

    override suspend fun getSuggestedFeeds(input: BskyGetSuggestedFeedsInput): BskyGetSuggestedFeedsResult {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.feed.getSuggestedFeeds") {
            parameter("limit", input.limit)

            if (input.cursor != null) {
                parameter("cursor", input.cursor)
            }

            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        if (response.status.value == 400 || response.status.value == 401) {
            // In this case we need to refresh the session ASAP.
            // Repeat the method call until we get a successful
            // response.
            refreshSessionIfNeeded(false)
            return getSuggestedFeeds(input)
        }

        return response.body()
    }

    override suspend fun getSuggestedFollowsByActor(actor: String): BskyGetSuggestedFollowsByActorResult {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.graph.getSuggestedFollowsByActor") {
            parameter("actor", actor)

            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        if (response.status.value == 400 || response.status.value == 401) {
            // In this case we need to refresh the session ASAP.
            // Repeat the method call until we get a successful
            // response.
            refreshSessionIfNeeded(false)
            return getSuggestedFollowsByActor(actor)
        }

        return response.body()
    }
}