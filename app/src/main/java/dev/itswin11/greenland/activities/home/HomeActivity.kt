package dev.itswin11.greenland.activities.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.itswin11.greenland.authDataStore
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flow : Flow<String> = authDataStore.data.map { preferences ->
            preferences.authInfoList[preferences.currentAccountIndex].did ?: ""
        }

        val handleFlow : Flow<String> = authDataStore.data.map { preferences ->
            preferences.authInfoList[preferences.currentAccountIndex].handle ?: ""
        }

        setContent {
            val userDid = flow.collectAsState(initial = null).value
            val handle = handleFlow.collectAsState(initial = null).value

            GreenlandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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