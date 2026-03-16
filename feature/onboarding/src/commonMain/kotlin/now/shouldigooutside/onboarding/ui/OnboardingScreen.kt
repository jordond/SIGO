package now.shouldigooutside.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.back
import now.shouldigooutside.core.resources.done
import now.shouldigooutside.core.resources.get_started
import now.shouldigooutside.core.resources.location_warning_dialog_text
import now.shouldigooutside.core.resources.next
import now.shouldigooutside.core.resources.warning
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.asContent
import now.shouldigooutside.core.ui.components.AlertDialog
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.IconButton
import now.shouldigooutside.core.ui.components.IconButtonVariant
import now.shouldigooutside.core.ui.components.Scaffold
import now.shouldigooutside.core.ui.components.Surface
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.components.snackbar.LocalSnackbarProvider
import now.shouldigooutside.core.ui.components.snackbar.Snackbar
import now.shouldigooutside.core.ui.components.snackbar.SnackbarHost
import now.shouldigooutside.core.ui.components.snackbar.SnackbarHostState
import now.shouldigooutside.core.ui.components.snackbar.rememberSnackbarProvider
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.ArrowLeft
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.onboarding.ui.OnboardingModel.Event
import now.shouldigooutside.onboarding.ui.location.LocationScreen
import now.shouldigooutside.onboarding.ui.navigation.OnboardingDestination
import now.shouldigooutside.onboarding.ui.navigation.OnboardingNavHost
import now.shouldigooutside.onboarding.ui.preferences.OnboardingPreferencesScreen
import now.shouldigooutside.onboarding.ui.summary.SummaryScreen
import now.shouldigooutside.onboarding.ui.units.OnboardingUnitsScreen
import now.shouldigooutside.onboarding.ui.welcome.WelcomeScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OnboardingScreen(
    parentNavController: NavHostController,
    onFinish: () -> Unit,
    model: OnboardingModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentEntry) {
        model.updateDestination(currentEntry)
    }

    val state by model.collectAsState()
    HandleEvents(model) { event ->
        when (event) {
            is Event.Finish -> onFinish
            is Event.ToScreen -> navController.navigate(event.destination)
        }
    }

    val snackbarProvider = rememberSnackbarProvider()
    Box {
        OnboardingScreen(
            currentDestination = state.currentDestination,
            onNext = model::onClick,
            onBack = navController::popBackStack,
            snackbarHostState = snackbarProvider.hostState,
        ) {
            CompositionLocalProvider(LocalSnackbarProvider provides snackbarProvider) {
                OnboardingNavHost(
                    navController = navController,
                    parentNavController = parentNavController,
                )
            }
        }

        if (state.showLocationWarning) {
            AlertDialog(
                title = Res.string.warning.get(),
                text = Res.string.location_warning_dialog_text.get(),
                onDismissRequest = { model.confirmLocationDialog(false) },
                onConfirmClick = { model.confirmLocationDialog(true) },
            )
        }
    }
}

@Composable
internal fun OnboardingScreen(
    currentDestination: OnboardingDestination,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNext: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = AppTheme.colors.error,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                },
            )
        },
        bottomBar = {
            Card(
                shape = RectangleShape,
                border = null,
                modifier = Modifier.height(130.dp),
            ) {
                HorizontalDivider(thickness = 6.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding()
                        .fillMaxSize(),
                ) {
                    AnimatedVisibility(
                        currentDestination !is OnboardingDestination.Welcome,
                    ) {
                        Row {
                            IconButton(
                                variant = IconButtonVariant.SecondaryElevated,
                                onClick = onBack,
                                modifier = Modifier.size(60.dp),
                            ) {
                                Icon(
                                    icon = AppIcons.Lucide.ArrowLeft,
                                    contentDescription = Res.string.back.get(),
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                        }
                    }

                    Button(
                        variant = ButtonVariant.PrimaryElevated,
                        onClick = onNext,
                        minHeight = 60.dp,
                    ) {
                        Box(
                            modifier.fillMaxWidth(),
                        ) {
                            val text = remember(currentDestination) {
                                when (currentDestination) {
                                    is OnboardingDestination.Welcome -> Res.string.get_started
                                    is OnboardingDestination.Summary -> Res.string.done
                                    else -> Res.string.next
                                }
                            }
                            Text(
                                text = text.get().uppercase(),
                                style = AppTheme.typography.h2.asContent,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Surface(
                color = AppTheme.colors.background,
                modifier = Modifier.fillMaxSize(),
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun OnboardingScreenPreview(
    route: OnboardingDestination,
    isDarkTheme: Boolean = false,
) {
    AppPreview(isDarkTheme = isDarkTheme) {
        OnboardingScreen(currentDestination = route) {
            when (route) {
                OnboardingDestination.Welcome -> {
                    WelcomeScreen()
                }
                OnboardingDestination.Units -> {
                    OnboardingUnitsScreen(
                        units = Units.Metric,
                        update = {},
                    )
                }
                OnboardingDestination.Preferences -> {
                    OnboardingPreferencesScreen(
                        preferences = Preferences.default,
                        updatePreferences = {},
                    )
                }
                OnboardingDestination.Location -> {
                    LocationScreen(location = null)
                }
                OnboardingDestination.Summary -> {
                    SummaryScreen()
                }
            }
        }
    }
}

@Preview
@Composable
private fun WelcomePreview() {
    OnboardingScreenPreview(OnboardingDestination.Welcome)
}

@Preview
@Composable
private fun UnitsPreview() {
    OnboardingScreenPreview(OnboardingDestination.Units)
}

@Preview
@Composable
private fun PreferencesPreview() {
    OnboardingScreenPreview(OnboardingDestination.Preferences)
}

@Preview
@Composable
private fun LocationPreview() {
    OnboardingScreenPreview(OnboardingDestination.Location)
}

@Preview
@Composable
private fun SummaryPreview() {
    OnboardingScreenPreview(OnboardingDestination.Summary)
}
