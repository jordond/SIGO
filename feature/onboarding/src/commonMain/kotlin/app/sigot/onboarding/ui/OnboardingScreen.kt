package app.sigot.onboarding.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.snackbar.LocalSnackbarProvider
import app.sigot.core.ui.components.snackbar.SnackbarHost
import app.sigot.core.ui.components.snackbar.SnackbarHostState
import app.sigot.core.ui.components.snackbar.rememberSnackbarProvider
import app.sigot.core.ui.preview.AppPreview
import app.sigot.onboarding.ui.navigation.OnboardingNavHost
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OnboardingScreen(
    parentNavController: NavHostController,
    onFinish: () -> Unit,
    model: OnboardingModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val snackbarProvider = rememberSnackbarProvider()

    val state by model.collectAsState()

    HandleEvents(model) { event -> }

    OnboardingScreen(
        snackbarHostState = snackbarProvider.hostState,
    ) {
        CompositionLocalProvider(LocalSnackbarProvider provides snackbarProvider) {
            OnboardingNavHost(
                navController = navController,
                parentNavController = parentNavController,
            )
        }
    }
}

@Composable
internal fun OnboardingScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppPreview {
        OnboardingScreen {
            Text("Content")
        }
    }
}
