package dev.itswin11.greenland.main

import dev.itswin11.greenland.models.AtProtoCreateSessionResult

interface IAtProtoClient {
    /**
     * Creates a session with the AT Protocol server.
     */
    suspend fun createSession(server: String, identifier: String, password: String): AtProtoCreateSessionResult

    /**
     * Automatically renews the AT Protocol session if needed.
     */
    suspend fun renewSessionIfNeeded(server: String)
}