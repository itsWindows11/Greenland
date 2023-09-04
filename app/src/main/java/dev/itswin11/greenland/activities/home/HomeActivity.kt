package dev.itswin11.greenland.activities.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.itswin11.greenland.constants.SettingsConstants
import dev.itswin11.greenland.helpers.authDataStore
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this, "HomeActivity", Toast.LENGTH_SHORT).show()

        val flow : Flow<String> = authDataStore.data.map { preferences ->
            preferences[SettingsConstants.CURRENT_USER_DID] ?: ""
        }

        val handleFlow : Flow<String> = authDataStore.data.map { preferences ->
            preferences[SettingsConstants.CURRENT_USER_HANDLE] ?: ""
        }

        setContent {
            GreenlandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var userDid by remember { mutableStateOf("") }
                    var handle by remember { mutableStateOf("") }

                    flow.collectAsState(initial = "").value.let {
                        userDid = it
                    }

                    handleFlow.collectAsState(initial = "").value.let {
                        handle = it
                    }

                    Greeting(name = "$userDid ($handle)")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GreenlandTheme {
        Greeting("Android")
    }
}