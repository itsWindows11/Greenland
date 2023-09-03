package dev.itswin11.greenland.main.impl

import dev.itswin11.greenland.main.IAtProtoClient
import dev.itswin11.greenland.models.AtProtoCreateSessionResult
import dev.itswin11.greenland.models.AtProtoSessionCredentials
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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
        val response = httpClient.post("https://$server/xrpc/com.atproto.server.createSession") {
            contentType(ContentType.Application.Json)
            setBody(AtProtoSessionCredentials(identifier, password))
        }

        return response.body()
    }
}