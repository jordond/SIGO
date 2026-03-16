package now.shouldigooutside.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalHaptics
import now.shouldigooutside.core.ui.LocalUse24HourTime
import now.shouldigooutside.core.ui.LocalWindowSizeClass
import now.shouldigooutside.core.ui.calculateWindowSizeClass
import now.shouldigooutside.core.ui.navigation.bottomsheet.BottomSheetNavigator
import now.shouldigooutside.core.ui.navigation.bottomsheet.ModalBottomSheetLayout
import now.shouldigooutside.core.ui.navigation.bottomsheet.rememberBottomSheetNavigator
import now.shouldigooutside.core.ui.rememberHaptics
import now.shouldigooutside.ui.AppHostModel.State.UiState
import now.shouldigooutside.ui.navigation.AppNavHost
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AppHost(
    model: AppHostModel = koinViewModel(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(),
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(skipPartiallyExpanded = true),
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
) {
    val state by model.collectAsState()

    val systemDarkMode = isSystemInDarkTheme()
    val isDarkTheme = remember(systemDarkMode, state.settings.themeMode) {
        when (state.settings.themeMode) {
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
            ThemeMode.System -> systemDarkMode
        }
    }

    AppTheme(isDarkTheme = isDarkTheme) {
        when (val uiState = state.uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    // Image(
                    //     painter = painterResource(Res.drawable.app_icon),
                    //     contentDescription = Res.string.logo.get(),
                    //     modifier = Modifier.size(84.dp),
                    // )
                }
            }
            is UiState.Loaded -> {
                val haptics = rememberHaptics(state.enableHaptics)

                CompositionLocalProvider(
                    LocalWindowSizeClass provides windowSizeClass,
                    LocalHaptics provides haptics,
                    LocalUse24HourTime provides state.settings.use24HourFormat,
                ) {
                    ModalBottomSheetLayout(
                        bottomSheetNavigator = bottomSheetNavigator,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        AppNavHost(
                            navController = navController,
                            startDestination = uiState.startDestination,
                        )
                    }
                }
            }
        }
    }
}
