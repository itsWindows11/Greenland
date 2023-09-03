package dev.itswin11.greenland.helpers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import dev.itswin11.greenland.main.IAtProtoClient
import dev.itswin11.greenland.main.impl.AtProtoClient
import java.util.prefs.Preferences

sealed class Global {
    companion object {
        val AtProtoClient: IAtProtoClient = AtProtoClient()
    }
}