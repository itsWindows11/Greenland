package dev.itswin11.greenland.main.impl

import dev.itswin11.greenland.App
import dev.itswin11.greenland.authDataStore
import dev.itswin11.greenland.main.IAtProtoClient
import dev.itswin11.greenland.models.AtProtoCreateSessionResult
import dev.itswin11.greenland.models.AtProtoRefreshSessionInfo
import dev.itswin11.greenland.models.AtProtoSessionCredentials
import dev.itswin11.greenland.models.BskyGetTimelineInput
import dev.itswin11.greenland.models.BskyGetTimelineResult
import dev.itswin11.greenland.protobuf.AuthInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
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

class AtProtoClient : IAtProtoClient {
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun createSession(server: String, identifier: String, password: String): AtProtoCreateSessionResult {
        val response = httpClient.post("https://$server/xrpc/com.atproto.server.createSession") {
            contentType(ContentType.Application.Json)
            setBody(AtProtoSessionCredentials(identifier, password))
        }

        return response.body()
    }

    override suspend fun refreshSessionIfNeeded(server: String) {
        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()

        val accessInfo = App.instance.tokenService.getCurrentAccessTokenInfo(currentAccountIndex)
        val info = App.instance.tokenService.getCurrentRefreshTokenInfo(currentAccountIndex)

        val did = App.instance.tokenService.getCurrentDid(currentAccountIndex)
        val handle = App.instance.tokenService.getCurrentHandle(currentAccountIndex)

        if ((accessInfo != null
                    && accessInfo.expiresAt != null
                    && accessInfo.expiresAt!!.time > Calendar.getInstance().time.time)
            || handle == null
            || did == null) {
            // We do not need to renew the session.
            // And if handle and did are null, we do not
            // need to refresh the session either since
            // we cannot pass the necessary info.
            return
        }

        val result = httpClient.post("https://$server/xrpc/com.atproto.server.refreshSession") {
            contentType(ContentType.Application.Json)
            setBody(AtProtoRefreshSessionInfo(accessInfo.toString(), info.toString(), handle, did))
        }.body<AtProtoCreateSessionResult>()

        App.instance.authDataStore.updateData {
            val builder = it.toBuilder()

            val authInfoBuilder = AuthInfo.getDefaultInstance().toBuilder()

            authInfoBuilder.accessJwt = result.accessJwt
            authInfoBuilder.refreshJwt = result.refreshJwt
            authInfoBuilder.did = result.did
            authInfoBuilder.handle = result.handle
            authInfoBuilder.signedIn = true

            builder.signedIn = true

            return@updateData builder.build()
        }
    }

    override suspend fun getHomeTimeline(server: String, input: BskyGetTimelineInput): BskyGetTimelineResult {
        refreshSessionIfNeeded(server)

        val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.first()
        val accessInfo = App.instance.authDataStore.data
            .map { preferences -> preferences.authInfoList[currentAccountIndex].accessJwt }.first()

        val response = httpClient.get("https://$server/xrpc/app.bsky.feed.getTimeline") {
            contentType(ContentType.Application.Json)
            setBody(input)

            headers {
                append(HttpHeaders.Authorization, "Bearer $accessInfo")
            }
        }

        return response.body()
    }
}