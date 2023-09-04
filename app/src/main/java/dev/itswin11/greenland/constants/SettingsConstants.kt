package dev.itswin11.greenland.constants

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class SettingsConstants {
    companion object {
        val ACCESS_JWT = stringPreferencesKey("access_jwt")
        val REFRESH_JWT = stringPreferencesKey("refresh_jwt")
        val CURRENT_USER_DID = stringPreferencesKey("current_user_did")
        val CURRENT_USER_HANDLE = stringPreferencesKey("current_user_handle")
        val SIGNED_IN = booleanPreferencesKey("signed_in")
    }
}