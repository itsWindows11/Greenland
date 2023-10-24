package dev.itswin11.greenland.activities.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import dev.itswin11.greenland.models.navigation.BottomNavigationItem
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import dev.itswin11.greenland.views.home.HomeView

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            GreenlandTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(WindowInsets(0.dp)),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var navigationSelectedItem by remember { mutableIntStateOf(0) }

                    Column(modifier = Modifier.fillMaxSize()) {
                        HomeView(modifier = Modifier.weight(1f))

                        NavigationBar {
                            BottomNavigationItem.items.forEachIndexed { index, navigationItem ->
                                NavigationBarItem(
                                    selected = index == navigationSelectedItem,
                                    label = {
                                        Text(
                                            navigationItem.label,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            navigationItem.icon,
                                            contentDescription = navigationItem.label
                                        )
                                    },
                                    onClick = {
                                        navigationSelectedItem = index
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun timeAgo(time: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - time
    val second = 1000L
    val minute = 60 * second
    val hour = 60 * minute
    val day = 24 * hour
    val week = 7 * day
    val month = 4 * week
    val year = 12 * month

    return when {
        diff < minute -> "now"
        diff < 2 * minute -> "m"
        diff < hour -> "${diff / minute}m"
        diff < 2 * hour -> "h"
        diff < day -> "${diff / hour}h"
        diff < 2 * day -> "1d"
        diff < week -> "${diff / day}d"
        diff < 2 * week -> "w"
        diff < month -> "${diff / week}w"
        diff < 2 * month -> "mo"
        diff < year -> "${diff / month}mo"
        else -> "${diff / year}y"
    }
}