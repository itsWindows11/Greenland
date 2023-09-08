package dev.itswin11.greenland.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import dev.itswin11.greenland.protobuf.AuthInfoContainer
import java.io.InputStream
import java.io.OutputStream

object AuthInfoContainerSerializer : Serializer<AuthInfoContainer> {
    override val defaultValue: AuthInfoContainer = AuthInfoContainer.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AuthInfoContainer {
        try {
            return AuthInfoContainer.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: AuthInfoContainer,
        output: OutputStream) = t.writeTo(output)
}