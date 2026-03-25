package now.shouldigooutside.ui.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.ui.navigation.Route
import now.shouldigooutside.ui.home.HomeScreen

@Serializable
internal data object HomeRoute : Route

internal fun NavGraphBuilder.homeScreen(controller: NavHostController) {
    composable<HomeRoute> {
        HomeScreen(
            navController = controller,
        )
    }
}
