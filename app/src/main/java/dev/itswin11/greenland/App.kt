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
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.defaultRequest
import kotlinx.serialization.json.Json

val Context.authDataStore: DataStore<AuthInfoContainer> by dataStore(
    fileName = "auth.pb",
    serializer = AuthInfoContainerSerializer
)

class App : Application() {
    companion object {
        lateinit var instance: App

        val jsonSerializer = Json {
            ignoreUnknownKeys = true
        }
        var httpClient = createHttpClient {}
        var atProtoClient: IAtProtoClient = AtProtoClient(httpClient)

        private fun createHttpClient(stuffToDo: HttpClientConfig<*>.() -> Unit): HttpClient {
            return HttpClient {
                install(HttpRequestRetry) {
                    retryOnServerErrors(5)
                }

                install(ContentEncoding) {
                    gzip()
                    deflate()
                }

                defaultRequest {
                    url("https://bsky.social")
                }

                install(HttpTimeout) {
                    requestTimeoutMillis = 20000
                }

                stuffToDo(this)
            }
        }

        fun createHttpClientWithAuth(accessToken: String, refreshToken: String): HttpClient {
            return createHttpClient {
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(accessToken, refreshToken)
                        }

                        sendWithoutRequest { true }
                    }
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