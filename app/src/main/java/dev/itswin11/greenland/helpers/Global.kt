package dev.itswin11.greenland.helpers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dev.itswin11.greenland.main.IAtProtoClient
import dev.itswin11.greenland.main.impl.AtProtoClient
import dev.itswin11.greenland.protobuf.AuthInfoContainer
import dev.itswin11.greenland.serializers.AuthInfoContainerSerializer

val Context.authDataStore: DataStore<AuthInfoContainer> by dataStore(
    fileName = "auth.pb",
    serializer = AuthInfoContainerSerializer
)

sealed class Global {
    companion object {
        val atProtoClient: IAtProtoClient = AtProtoClient()
    }
}