package dev.itswin11.greenland.services

import android.content.Context
import com.auth0.android.jwt.JWT
import dev.itswin11.greenland.authDataStore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TokenService(private val context: Context) {
    suspend fun getCurrentDid(accountIndex: Int): String? = coroutineScope {
        val flow : Flow<String?> = context.authDataStore.data.map { preferences ->
            preferences.authInfoList[accountIndex].did
        }

        return@coroutineScope flow.stateIn(this).value
    }

    suspend fun getCurrentHandle(accountIndex: Int): String? = coroutineScope {
        val flow : Flow<String?> = context.authDataStore.data.map { preferences ->
            preferences.authInfoList[accountIndex].handle
        }

        return@coroutineScope flow.stateIn(this).value
    }

    suspend fun getCurrentAccessTokenInfo(accountIndex: Int): JWT? = coroutineScope {
        val accessJwtFlow : Flow<String?> = context.authDataStore.data.map { preferences ->
            preferences.authInfoList[accountIndex].accessJwt
        }

        val accessJwt = accessJwtFlow.stateIn(this).value ?: return@coroutineScope null

        return@coroutineScope JWT(accessJwt)
    }

    suspend fun getCurrentRefreshTokenInfo(accountIndex: Int): JWT? = coroutineScope {
        val accessJwtFlow : Flow<String?> = context.authDataStore.data.map { preferences ->
            preferences.authInfoList[accountIndex].refreshJwt
        }

        val accessJwt = accessJwtFlow.stateIn(this).value ?: return@coroutineScope null
        return@coroutineScope JWT(accessJwt)
    }
}