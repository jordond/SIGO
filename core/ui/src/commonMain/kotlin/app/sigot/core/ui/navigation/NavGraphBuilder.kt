package app.sigot.core.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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

public data class NavAnimation(
    val enter: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    val exit: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    val popEnter: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enter,
    val popExit: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exit,
)

public val AnimatedContentTransitionScope<NavBackStackEntry>.SlideInUp: EnterTransition
    get() = slideIntoContainer(SlideDirection.Up, tween(700))

public val AnimatedContentTransitionScope<NavBackStackEntry>.SlideOutDown: ExitTransition
    get() = slideOutOfContainer(SlideDirection.Down, tween(700))

public val SlideEnter: EnterTransition
    get() = slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500))

public val SlideExit: ExitTransition
    get() = slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500))

public val SlidePopEnter: EnterTransition
    get() = slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500))

public val SlidePopExit: ExitTransition
    get() = slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500))

public object NavAnimations {
    public val None: NavAnimation = NavAnimation()

    public val SlideHorizontally: NavAnimation = NavAnimation(
        enter = { SlideEnter },
        exit = { SlideExit },
        popEnter = { SlidePopEnter },
        popExit = { SlidePopExit },
    )

    public val PopUp: NavAnimation = NavAnimation(
        enter = { SlideInUp },
        exit = { null },
        popEnter = { null },
        popExit = { SlideOutDown },
    )
}

public inline fun <reified T : Any> NavGraphBuilder.popUpScreen(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = NavAnimations.PopUp.enter,
        exitTransition = NavAnimations.PopUp.exit,
        popEnterTransition = NavAnimations.PopUp.popEnter,
        popExitTransition = NavAnimations.PopUp.popExit,
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
        enterTransition = NavAnimations.SlideHorizontally.enter,
        exitTransition = NavAnimations.SlideHorizontally.exit,
        popEnterTransition = NavAnimations.SlideHorizontally.popEnter,
        popExitTransition = NavAnimations.SlideHorizontally.popExit,
    )
}
