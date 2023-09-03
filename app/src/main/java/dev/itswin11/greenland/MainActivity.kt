package dev.itswin11.greenland

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.preferencesDataStore
import dev.itswin11.greenland.activities.auth.LoginActivity
import dev.itswin11.greenland.activities.auth.SignUpActivity
import dev.itswin11.greenland.enums.AuthActivityType
import dev.itswin11.greenland.ui.theme.GreenlandTheme

val Context.dataStore by preferencesDataStore(
    name = "auth"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GreenlandTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginOrSignUpView()
                }
            }
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun LoginOrSignUpView() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    content = { Text("Sign in with Bluesky") },
                    onClick = { startAuthActivity(AuthActivityType.Login) }
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    content = { Text("Create a new account") },
                    onClick = { startAuthActivity(AuthActivityType.SignUp) },
                    enabled = false
                )
            }
        }
    }

    private fun startAuthActivity(type: AuthActivityType) {
        if (type == AuthActivityType.Login) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        startActivity(Intent(this, SignUpActivity::class.java))
    }
}