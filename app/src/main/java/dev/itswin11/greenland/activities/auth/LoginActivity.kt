package dev.itswin11.greenland.activities.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.itswin11.greenland.activities.auth.ui.theme.GreenlandTheme
import dev.itswin11.greenland.activities.home.HomeActivity
import dev.itswin11.greenland.constants.SettingsConstants
import dev.itswin11.greenland.dataStore
import dev.itswin11.greenland.helpers.Global
import dev.itswin11.greenland.models.AtProtoSessionCredentials
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenlandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginView()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showSystemUi = true)
    @Composable
    fun LoginView() {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
            val handle = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }

            val coroutineScope = rememberCoroutineScope()

            Text(
                text = "Sign in with Bluesky",
                style = MaterialTheme.typography.headlineSmall
            )
            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                OutlinedTextField(
                    value = handle.value,
                    onValueChange = { handle.value = it },
                    label = { Text("Handle or email") }
                )
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("App password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
            Button(onClick = { coroutineScope.launch { startSignInFlow(handle.value, password.value) } }) {
                Text("Sign In")
            }
        }
    }

    private suspend fun startSignInFlow(handle: String, password: String) {
        val result = Global.AtProtoClient.createSession("bsky.social", handle, password)

        dataStore.edit {
            it[SettingsConstants.ACCESS_JWT] = result.accessJwt
            it[SettingsConstants.REFRESH_JWT] = result.refreshJwt
            it[SettingsConstants.CURRENT_USER_DID] = result.did
            it[SettingsConstants.CURRENT_USER_HANDLE] = result.handle
        }

        startActivity(Intent(this, HomeActivity::class.java))
    }
}