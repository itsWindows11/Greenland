package dev.itswin11.greenland.activities.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.itswin11.greenland.ui.theme.GreenlandTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenlandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignUpView()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showSystemUi = true)
    @Composable
    fun SignUpView() {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
            Text(
                text = "Create a Bluesky account",
                style = MaterialTheme.typography.headlineSmall
            )
            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Server") }
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("@handle") }
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Password") }
                )
            }
            Button(onClick = {}) {
                Text("Sign In")
            }
        }
    }
}