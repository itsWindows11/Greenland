package dev.itswin11.greenland.services

import android.content.Context
import com.auth0.android.jwt.JWT
import dev.itswin11.greenland.authDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenService(private val context: Context) {
    suspend fun getCurrentDid(accountIndex: Int): String? {
        val flow : Flow<String?> = context.authDataStore.data.map { preferences ->
            preferences.authInfoList[accountIndex].did
        }

        return flow.first()
    }

    suspend fun getCurrentHandle(accountIndex: Int): String? {
        val flow : Flow<String?> = context.authDataStore.data.map { preferences ->
            preferences.authInfoList[accountIndex].handle
        }

        return flow.first()
    }

    suspend fun getCurrentAccessTokenInfo(accountIndex: Int): JWT? {
        val accessJwtFlow : Flow<String?> = context.authDataStore.data.map { preferences ->
            preferences.authInfoList[accountIndex].accessJwt
        }

        val accessJwt = accessJwtFlow.first() ?: return null
        return JWT(accessJwt)
    }

    suspend fun getCurrentRefreshTokenInfo(accountIndex: Int): JWT?  {
        val accessJwtFlow : Flow<String?> = context.authDataStore.data.map { preferences ->
            preferences.authInfoList[accountIndex].refreshJwt
        }

        val accessJwt = accessJwtFlow.first() ?: return null
        return JWT(accessJwt)
    }
}