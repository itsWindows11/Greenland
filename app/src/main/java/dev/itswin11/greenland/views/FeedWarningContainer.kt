package dev.itswin11.greenland.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FeedWarningContainer(modifier: Modifier, text: String, contentToReplaceWith: (@Composable () -> Unit)? = null) {
    val contentReplaced = remember { mutableStateOf(false) }

    if (contentReplaced.value && contentToReplaceWith != null) {
        Column(modifier) {
            contentToReplaceWith()
        }
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                )
                .clickable { },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(Modifier.padding(8.dp)) {
                Text(text)

                if (contentToReplaceWith != null) {
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { contentReplaced.value = true }) {
                        Text("Show")
                    }
                }
            }
        }
    }
}