package dev.itswin11.greenland

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dev.itswin11.greenland.api.AtProtoClient
import dev.itswin11.greenland.api.IAtProtoClient
import dev.itswin11.greenland.protobuf.AuthInfoContainer
import dev.itswin11.greenland.serializers.AuthInfoContainerSerializer
import dev.itswin11.greenland.services.TokenService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val Context.authDataStore: DataStore<AuthInfoContainer> by dataStore(
    fileName = "auth.pb",
    serializer = AuthInfoContainerSerializer
)

class App : Application() {
    companion object {
        lateinit var instance: App

        val httpClient = createHttpClient()
        val atProtoClient: IAtProtoClient = AtProtoClient(httpClient)

        private fun createHttpClient(): HttpClient {
            return HttpClient {
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
        }
    }

    init {
        instance = this
    }

    val tokenService: TokenService = TokenService(this)

    override fun onCreate() {
        super.onCreate()
    }
}