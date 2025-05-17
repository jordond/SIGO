package app.sigot.core.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.reflect.KType

public inline fun <reified T : Any> NavGraphBuilder.popUpScreen(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = { slideIntoContainer(SlideDirection.Up, tween(700)) },
        exitTransition = { slideOutOfContainer(SlideDirection.Down, tween(700)) },
        content = content,
    )
}

public inline fun <reified T : Any> NavGraphBuilder.slideHorizontally(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        content = content,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500))
        },
    )
}
