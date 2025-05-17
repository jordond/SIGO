package app.sigot.ui

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
import app.sigot.core.model.ui.ThemeMode
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalHaptics
import app.sigot.core.ui.LocalWindowSizeClass
import app.sigot.core.ui.calculateWindowSizeClass
import app.sigot.core.ui.rememberHaptics
import app.sigot.ui.AppHostModel.State.UiState
import app.sigot.ui.navigation.AppNavHost
import dev.stateholder.extensions.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AppHost(
    model: AppHostModel = koinViewModel(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(),
    navController: NavHostController = rememberNavController(),
) {
    val state by model.collectAsState()

    val systemDarkMode = isSystemInDarkTheme()
    val isDarkTheme = remember(systemDarkMode, state.themeMode) {
        when (state.themeMode) {
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
                ) {
                    AppNavHost(
                        startDestination = uiState.startDestination,
                        navController = navController,
                    )
                }
            }
        }
    }
}
