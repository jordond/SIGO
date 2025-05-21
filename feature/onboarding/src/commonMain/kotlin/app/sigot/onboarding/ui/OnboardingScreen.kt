package app.sigot.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.Units
import app.sigot.core.resources.Res
import app.sigot.core.resources.back
import app.sigot.core.resources.done
import app.sigot.core.resources.get_started
import app.sigot.core.resources.location_warning_dialog_text
import app.sigot.core.resources.next
import app.sigot.core.resources.warning
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.asContent
import app.sigot.core.ui.components.AlertDialog
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Surface
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.components.snackbar.LocalSnackbarProvider
import app.sigot.core.ui.components.snackbar.SnackbarHost
import app.sigot.core.ui.components.snackbar.SnackbarHostState
import app.sigot.core.ui.components.snackbar.rememberSnackbarProvider
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.ArrowLeft
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import app.sigot.onboarding.ui.OnboardingModel.Event
import app.sigot.onboarding.ui.location.LocationScreen
import app.sigot.onboarding.ui.navigation.OnboardingDestination
import app.sigot.onboarding.ui.navigation.OnboardingNavHost
import app.sigot.onboarding.ui.preferences.PreferencesScreen
import app.sigot.onboarding.ui.summary.SummaryScreen
import app.sigot.onboarding.ui.units.UnitsScreen
import app.sigot.onboarding.ui.welcome.WelcomeScreen
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
    val layoutDirection = LocalLayoutDirection.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                ).fillMaxSize(),
        ) {
            Surface(
                color = AppTheme.colors.background,
                modifier = Modifier.weight(4f),
            ) {
                content()
            }

            Card(
                shape = RectangleShape,
                border = null,
                modifier = Modifier.weight(1f),
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
                    UnitsScreen(
                        units = Units.Metric,
                        update = {},
                    )
                }
                OnboardingDestination.Preferences -> {
                    PreferencesScreen(
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
