package dev.itswin11.greenland.models.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {
    companion object {
        val items get() = listOf(
            BottomNavigationItem("Home", Icons.Filled.Home, "home"),
            BottomNavigationItem("Search", Icons.Filled.Search, "search"),
            BottomNavigationItem("Feeds", Icons.Filled.RssFeed, "feeds"),
            BottomNavigationItem("Notifications", Icons.Filled.Notifications, "notifications"),
            BottomNavigationItem("Profile", Icons.Filled.AccountCircle, "profile")
        )
    }
}