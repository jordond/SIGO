package now.shouldigooutside.core.ui.navigation

import androidx.navigation.NavHostController

public fun NavHostController.canPop(): Boolean = previousBackStackEntry != null
