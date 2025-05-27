package app.sigot.core.ui.navigation.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavDestinationDsl
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.get
import co.touchlab.kermit.Logger
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Add the [content] [Composable] as bottom sheet content to the [NavGraphBuilder]
 *
 * @param route route for the destination
 * @param arguments list of arguments to associate with destination
 * @param deepLinks list of deep links to associate with the destinations
 * @param content the sheet content at the given destination
 */
public fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
) {
    val navigator = provider[BottomSheetNavigator::class]
    val destination = BottomSheetNavigator.Destination(navigator, content).apply {
        this.route = route
        arguments.fastForEach { (argumentName, argument) ->
            addArgument(argumentName, argument)
        }
        deepLinks.fastForEach { deepLink ->
            addDeepLink(deepLink)
        }
    }

    addDestination(destination)
}

/**
 * Adds a bottom sheet destination to the [NavGraphBuilder] using a type-safe route.
 *
 * This function allows you to define a bottom sheet where the route is derived from
 * the reified type `T`. This is particularly useful when using libraries that support
 * type-safe navigation.
 *
 * @param T The type to be used as the route for this destination. The route will be
 * generated based on the fully qualified name of this class.
 * @param deepLinks A list of [NavDeepLink]s to associate with this destination. Deep links
 * allow external URLs or URIs to navigate directly to this bottom sheet.
 * @param typeMap A map of [KType] to [NavType] to define custom argument types for this
 * destination. This is used by the navigation library to correctly parse
 *  arguments from the route.
 * @param content The composable content to be displayed within the bottom sheet. This lambda receives a
 * [ColumnScope] and a [NavBackStackEntry] which provides access to arguments and other navigation-related
 * information.
 */
public inline fun <reified T : Any> NavGraphBuilder.bottomSheet(
    deepLinks: List<NavDeepLink> = emptyList(),
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
) {
    val destination = BottomSheetNavigatorDestinationBuilder(
        navigator = provider[BottomSheetNavigator::class],
        route = T::class,
        typeMap = typeMap,
        content = content,
    ).apply {
        deepLinks.forEach { deepLink -> deepLink(deepLink) }
    }

    Logger.e {
        "adding bottom sheet destination: ${destination.route}"
    }
    destination(destination)
}

/**
 * DSL for constructing a new [ComposeNavigator.Destination].
 */
@NavDestinationDsl
public class BottomSheetNavigatorDestinationBuilder :
    NavDestinationBuilder<BottomSheetNavigator.Destination> {
    private val composeNavigator: BottomSheetNavigator
    private val content: @Composable ColumnScope.(NavBackStackEntry) -> Unit

    public constructor(
        navigator: BottomSheetNavigator,
        route: String,
        content: @Composable ColumnScope.(NavBackStackEntry) -> Unit,
    ) : super(navigator, route) {
        this.composeNavigator = navigator
        this.content = content
    }

    public constructor(
        navigator: BottomSheetNavigator,
        route: KClass<*>,
        typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
        content: @Composable ColumnScope.(NavBackStackEntry) -> Unit,
    ) : super(navigator, route, typeMap) {
        this.composeNavigator = navigator
        this.content = content
    }

    override fun instantiateDestination(): BottomSheetNavigator.Destination =
        BottomSheetNavigator.Destination(composeNavigator, content)
}
