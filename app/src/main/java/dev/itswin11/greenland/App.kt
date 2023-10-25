package dev.itswin11.greenland

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dev.itswin11.greenland.main.IAtProtoClient
import dev.itswin11.greenland.main.impl.AtProtoClient
import dev.itswin11.greenland.protobuf.AuthInfoContainer
import dev.itswin11.greenland.serializers.AuthInfoContainerSerializer
import dev.itswin11.greenland.services.TokenService

val Context.authDataStore: DataStore<AuthInfoContainer> by dataStore(
    fileName = "auth.pb",
    serializer = AuthInfoContainerSerializer
)

class App : Application() {
    companion object {
        lateinit var instance: App
        val atProtoClient: IAtProtoClient = AtProtoClient()
    }

    init {
        instance = this
    }

    val tokenService: TokenService = TokenService(this)

    override fun onCreate() {
        super.onCreate()
    }
}