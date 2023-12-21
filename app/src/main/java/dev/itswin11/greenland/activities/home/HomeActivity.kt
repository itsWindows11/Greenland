package dev.itswin11.greenland.activities.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.bsky.notification.GetUnreadCountQueryParams
import dev.itswin11.greenland.App
import dev.itswin11.greenland.R
import dev.itswin11.greenland.models.navigation.BottomNavigationItem
import dev.itswin11.greenland.ui.theme.GreenlandTheme
import dev.itswin11.greenland.util.popupComposableWithTriggerCase
import dev.itswin11.greenland.util.popupPageComposable
import dev.itswin11.greenland.util.secondaryPageComposable
import dev.itswin11.greenland.views.explore.ExploreView
import dev.itswin11.greenland.views.home.HomeView
import dev.itswin11.greenland.views.notifications.NotificationsView
import dev.itswin11.greenland.views.profile.ProfileFollowingView
import dev.itswin11.greenland.views.profile.ProfileView
import sh.christian.ozone.api.AtIdentifier
import soup.compose.material.motion.animation.rememberSlideDistance

class HomeActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
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
                    var notificationCount by remember { mutableIntStateOf(0) }
                    val navController = rememberNavController()

                    LaunchedEffect(Unit) {
                        try {
                            notificationCount =
                                App.atProtoClient
                                    .getUnreadCount(GetUnreadCountQueryParams(null))
                                    .requireResponse()
                                    .count
                                    .toInt()
                        } catch (_: Exception) {
                            // pass
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        AppNavHost(Modifier.weight(1f), navController)

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
                                        if (navigationItem.route == "notifications" && notificationCount > 0) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(Modifier.offset((-4).dp, 6.dp)) {
                                                        Text(if (notificationCount > 99) "99+" else notificationCount.toString())
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    navigationItem.icon,
                                                    contentDescription = navigationItem.label
                                                )
                                            }
                                        } else {
                                            Icon(
                                                navigationItem.icon,
                                                contentDescription = navigationItem.label
                                            )
                                        }
                                    },
                                    onClick = {
                                        navigationSelectedItem = index
                                        navController.navigate(navigationItem.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true

                                            anim {
                                                enter = R.animator.empty_animation
                                                exit = R.animator.empty_animation
                                                popEnter = R.animator.empty_animation
                                                popExit = R.animator.empty_animation
                                            }
                                        }
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

@Composable
fun AppNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val slideDistance = rememberSlideDistance()

    NavHost(
        modifier = modifier,
        startDestination = BottomNavigationItem.Home.route,
        navController = navController,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(BottomNavigationItem.Home.route) {
            HomeView { post ->
                // TODO: implement thread view.
            }
        }
        composable(BottomNavigationItem.Explore.route) {
            ExploreView()
        }
        composable(BottomNavigationItem.Notifications.route) {
            NotificationsView()
        }
        popupComposableWithTriggerCase(
            BottomNavigationItem.Profile.route,
            isRtl,
            slideDistance,
            enterAnimTriggerCase = {
                /*it.initialState.destination.route?.startsWith("followerView") == true
                        || it.initialState.destination.route?.startsWith("followingView") == true*/
                false
            },
            exitAnimTriggerCase = {
                it.targetState.destination.route?.startsWith("followerView") == true
                        || it.targetState.destination.route?.startsWith("followingView") == true
            }
        ) {
            ProfileView(
                onFollowingClicked = {
                    navController.navigate("followingView/${it.atIdentifier}")
                },
                onFollowerClicked = {
                    navController.navigate("followerView/${it.atIdentifier}")
                }
            )
        }

        secondaryPageComposable(BottomNavigationItem.PostView.route, isRtl, slideDistance) {
            // TODO
        }

        popupPageComposable(
            "${BottomNavigationItem.FollowerView.route}/{atIdentifier}",
            isRtl,
            slideDistance,
            arguments = listOf(navArgument("atIdentifier") {
                type = NavType.StringType
            })) {
            it.arguments?.getString("atIdentifier")?.let { profileDid ->
                ProfileFollowingView(
                    identifier = AtIdentifier(profileDid),
                    isFollowingPage = false,
                    onBackRequested = { navController.popBackStack() }
                )
            }
        }
        popupPageComposable(
            "${BottomNavigationItem.FollowingView.route}/{atIdentifier}",
            isRtl,
            slideDistance,
            arguments = listOf(navArgument("atIdentifier") {
                type = NavType.StringType
            })) {
            it.arguments?.getString("atIdentifier")?.let { profileDid ->
                ProfileFollowingView(
                    identifier = AtIdentifier(profileDid),
                    isFollowingPage = true,
                    onBackRequested = { navController.popBackStack() }
                )
            }
        }
    }
}

fun timeAgo(time: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - (time * 1000)
    val second = 1000L
    val minute = 60 * second
    val hour = 60 * minute
    val day = 24 * hour
    val week = 7 * day
    val month = 4 * week
    val year = 12 * month

    return when {
        diff < minute -> "now"
        diff < 2 * minute -> "1m"
        diff < hour -> "${diff / minute}m"
        diff < 2 * hour -> "1h"
        diff < day -> "${diff / hour}h"
        diff < 2 * day -> "1d"
        diff < week -> "${diff / day}d"
        diff < 2 * week -> "1w"
        diff < month -> "${diff / week}w"
        diff < 2 * month -> "1mo"
        diff < year -> "${diff / month}mo"
        else -> "${diff / year}y"
    }
}