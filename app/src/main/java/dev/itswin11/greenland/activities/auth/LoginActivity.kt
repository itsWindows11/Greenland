package dev.itswin11.greenland.activities.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.atproto.server.CreateSessionRequest
import com.atproto.server.CreateSessionResponse
import dev.itswin11.greenland.App
import dev.itswin11.greenland.activities.home.HomeActivity
import dev.itswin11.greenland.api.AtProtoClient
import dev.itswin11.greenland.authDataStore
import dev.itswin11.greenland.protobuf.AuthInfo
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.launch
import sh.christian.ozone.api.response.AtpResponse

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            GreenlandTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginView()
                }
            }
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun LoginView() {
        Column(
            modifier = Modifier.imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            val handle = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }

            val showPassword = remember { mutableStateOf(false) }

            val visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation()

            val coroutineScope = rememberCoroutineScope()

            Text(
                text = "Sign in with Bluesky",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
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
                    label = { Text("Password") },
                    visualTransformation = visualTransformation,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showPassword.value = !showPassword.value }) {
                            Icon(
                                if (showPassword.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Visibility",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
            Button(onClick = { coroutineScope.launch { startSignInFlow(handle.value, password.value) } }) {
                Text("Sign In")
            }
        }
    }

    private suspend fun startSignInFlow(handle: String, password: String) {
        val result: AtpResponse<CreateSessionResponse>

        try {
            result = App.atProtoClient.createSession(CreateSessionRequest(handle, password))
        } catch (e: IOException) {
            Toast.makeText(this, "We couldn't sign in to this account right now. Try again later.", Toast.LENGTH_SHORT).show()
            return
        }

        if (result is AtpResponse.Failure) {
            result.error?.error?.let {
                if (it == "AuthenticationRequired") {
                    Toast.makeText(this, "The identifier (handle or email) or password is incorrect.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "We couldn't sign in to this account right now. Try again later.", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }

        val response = result.requireResponse()

        authDataStore.updateData {
            val builder = it.toBuilder()

            val authInfoBuilder = AuthInfo.newBuilder()

            authInfoBuilder.accessJwt = response.accessJwt
            authInfoBuilder.refreshJwt = response.refreshJwt
            authInfoBuilder.did = response.did.did
            authInfoBuilder.handle = response.handle.handle

            val authInfo = authInfoBuilder.build()

            builder.authInfoList.toList().forEachIndexed { index, _ ->
                /*if (item.signedIn) {
                    val itemBuilder = item.toBuilder()
                    itemBuilder.signedIn = false
                    builder.authInfoList[index] = itemBuilder.build()
                }*/

                // TODO: Remove this when we fully add account management
                builder.removeAuthInfo(index)
            }

            builder.addAuthInfo(authInfo)
            builder.currentAccountIndex = builder.authInfoList.size - 1

            return@updateData builder.build()
        }

        App.httpClient = App.createHttpClientWithAuth(response.accessJwt, response.refreshJwt)
        App.atProtoClient = AtProtoClient(App.httpClient)

        startActivity(Intent(this, HomeActivity::class.java))
    }
}