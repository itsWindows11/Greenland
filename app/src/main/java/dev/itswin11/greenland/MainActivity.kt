package dev.itswin11.greenland

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import dev.itswin11.greenland.ui.theme.GreenlandTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenlandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val textState = remember { mutableStateOf("") }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(Dp(8F)),
                        modifier = Modifier.padding(Dp(12F))
                    ) {
                        Text("Hello World!")
                        TextField(
                            value = textState.value,
                            onValueChange = { value: String -> textState.value = value},
                            label = { Text("Test placeholder") }
                        )
                        OutlinedTextField(
                            value = textState.value,
                            onValueChange = { value: String -> textState.value = value},
                            label = { Text("Test placeholder") }
                        )
                        Button(
                            onClick = {},
                            content = { Text("Test button") }
                        )
                    }
                }
            }
        }
    }
}