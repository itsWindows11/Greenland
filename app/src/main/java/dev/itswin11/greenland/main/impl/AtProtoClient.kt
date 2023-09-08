package dev.itswin11.greenland.main.impl

import dev.itswin11.greenland.App
import dev.itswin11.greenland.helpers.authDataStore
import dev.itswin11.greenland.main.IAtProtoClient
import dev.itswin11.greenland.models.AtProtoCreateSessionResult
import dev.itswin11.greenland.models.AtProtoRefreshSessionInfo
import dev.itswin11.greenland.models.AtProtoSessionCredentials
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Calendar

class AtProtoClient : IAtProtoClient {
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun createSession(server: String, identifier: String, password: String): AtProtoCreateSessionResult {
        return withContext(Dispatchers.IO) {
            //renewSessionIfNeeded(server)
            val response = httpClient.post("https://$server/xrpc/com.atproto.server.createSession") {
                contentType(ContentType.Application.Json)
                setBody(AtProtoSessionCredentials(identifier, password))
            }

            return@withContext response.body()
        }
    }

    override suspend fun renewSessionIfNeeded(server: String) {
        return withContext(Dispatchers.IO) {
            val currentAccountIndex = App.instance.authDataStore.data.map { it.currentAccountIndex }.stateIn(this).value

            val accessInfo = App.instance.tokenService.getCurrentRefreshTokenInfo(currentAccountIndex)
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
                return@withContext
            }

            val response = httpClient.post("https://$server/xrpc/com.atproto.server.createSession") {
                contentType(ContentType.Application.Json)
                setBody(AtProtoRefreshSessionInfo(accessInfo.toString(), info.toString(), handle, did))
            }

            return@withContext response.body()
        }
    }
}