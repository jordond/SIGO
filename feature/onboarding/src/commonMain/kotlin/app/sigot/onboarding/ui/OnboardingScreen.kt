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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.Units
import app.sigot.core.resources.Res
import app.sigot.core.resources.back
import app.sigot.core.resources.done
import app.sigot.core.resources.lets_go
import app.sigot.core.resources.next
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.asContent
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
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
import app.sigot.core.ui.icons.lucide.ArrowRight
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
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

private val destinations = listOfNotNull(
    OnboardingDestination.Welcome,
    OnboardingDestination.Units,
    OnboardingDestination.Preferences,
    OnboardingDestination.Location,
    OnboardingDestination.Summary,
)

@Composable
internal fun OnboardingScreen(
    parentNavController: NavHostController,
    onFinish: () -> Unit,
    model: OnboardingModel = koinViewModel(),
) {
    val snackbarProvider = rememberSnackbarProvider()

    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentDestination = remember(currentEntry) {
        destinations.firstOrNull { destination ->
            currentEntry?.destination?.hasRoute(destination::class) == true
        } ?: OnboardingDestination.Welcome
    }

    val state by model.collectAsState()

    HandleEvents(model) { event -> }

    OnboardingScreen(
        currentDestination = currentDestination,
        onNext = {
            val index = destinations.indexOf(currentDestination)
            if (index == destinations.lastIndex) {
                onFinish()
            } else if (index != -1) {
                navController.navigate(destinations[index + 1])
            }
        },
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
                modifier = Modifier.weight(3f),
            ) {
                content()
            }

            Card(
                shape = RectangleShape,
                modifier = Modifier.weight(1f),
            ) {
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
                                    is OnboardingDestination.Welcome -> Res.string.lets_go
                                    is OnboardingDestination.Summary -> Res.string.done
                                    else -> Res.string.next
                                }
                            }
                            Text(
                                text = text.get().uppercase(),
                                style = AppTheme.typography.h2.asContent,
                                modifier = Modifier.align(Alignment.Center),
                            )

                            Icon(
                                icon = AppIcons.Lucide.ArrowRight,
                                modifier = Modifier.align(Alignment.CenterEnd),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun OnboardingScreenPreview(route: OnboardingDestination) {
    AppPreview {
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
                    LocationScreen(
                        enableLocationUpdates = true,
                        location = null,
                        toggleLocationUpdates = { },
                        getCurrentLocation = { },
                    )
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
