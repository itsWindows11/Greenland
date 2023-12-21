package dev.itswin11.greenland.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.materialSharedAxisYIn
import soup.compose.material.motion.animation.materialSharedAxisYOut

fun NavGraphBuilder.secondaryPageComposable(
    route: String,
    isRtl: Boolean,
    slideDistance: Int,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        arguments,
        deepLinks,
        enterTransition = { materialSharedAxisXIn(!isRtl, slideDistance) },
        exitTransition = { materialSharedAxisXOut(!isRtl, slideDistance) },
        popEnterTransition = { materialSharedAxisXIn(isRtl, slideDistance) },
        popExitTransition = { materialSharedAxisXOut(isRtl, slideDistance) },
    ) { backStackEntry ->
        content(backStackEntry)
    }
}

fun NavGraphBuilder.popupComposableWithTriggerCase(
    route: String,
    isRtl: Boolean,
    slideDistance: Int,
    enterAnimTriggerCase: (AnimatedContentTransitionScope<NavBackStackEntry>) -> Boolean,
    exitAnimTriggerCase: (AnimatedContentTransitionScope<NavBackStackEntry>) -> Boolean,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        arguments,
        deepLinks,
        popEnterTransition = {
            if (enterAnimTriggerCase(this)) {
                materialSharedAxisXIn(isRtl, slideDistance)
            } else {
                EnterTransition.None
            }
        },
        popExitTransition = {
            if (exitAnimTriggerCase(this)) {
                materialSharedAxisXOut(isRtl, slideDistance)
            } else {
                ExitTransition.None
            }
        },
    ) { backStackEntry ->
        content(backStackEntry)
    }
}

fun NavGraphBuilder.popupPageComposable(
    route: String,
    isRtl: Boolean,
    slideDistance: Int,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        arguments,
        deepLinks,
        enterTransition = { materialSharedAxisYIn(!isRtl, slideDistance) },
        exitTransition = { materialSharedAxisYOut(!isRtl, slideDistance) },
        popEnterTransition = { materialSharedAxisYIn(isRtl, slideDistance) },
        popExitTransition = { materialSharedAxisYOut(isRtl, slideDistance) },
    ) { backStackEntry ->
        content(backStackEntry)
    }
}