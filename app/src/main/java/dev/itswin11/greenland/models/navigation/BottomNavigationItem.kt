package dev.itswin11.greenland.models.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String = "",
    val badgeCount: Int? = null
) {
    companion object {
        val Home = BottomNavigationItem("Home", Icons.Filled.Home, "home")
        val Explore = BottomNavigationItem("Explore", Icons.Filled.Search, "explore")
        val Notifications = BottomNavigationItem("Notifications", Icons.Filled.Notifications, "notifications")
        val Profile = BottomNavigationItem("Profile", Icons.Filled.AccountCircle, "profile")
        val PostView = BottomNavigationItem(route = "post")
        val FollowerView = BottomNavigationItem(route = "followerView")
        val FollowingView = BottomNavigationItem(route = "followingView")

        val items get() = listOf(
            Home,
            Explore,
            Notifications,
            Profile
        )
    }
}