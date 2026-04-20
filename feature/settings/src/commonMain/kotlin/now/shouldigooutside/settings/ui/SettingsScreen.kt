package now.shouldigooutside.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.dispatcher.rememberRelayOf
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.Version
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.platform.launchAppStore
import now.shouldigooutside.core.platform.shareApp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.settings
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Scaffold
import now.shouldigooutside.core.ui.components.ScaffoldScope.innerPadding
import now.shouldigooutside.core.ui.components.snackbar.Snackbar
import now.shouldigooutside.core.ui.components.snackbar.SnackbarHost
import now.shouldigooutside.core.ui.components.snackbar.SnackbarHostState
import now.shouldigooutside.core.ui.components.snackbar.rememberSnackbarProvider
import now.shouldigooutside.core.ui.components.topbar.TopBarDefaults
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.settings.ui.components.SettingsTopBar
import now.shouldigooutside.settings.ui.section.AboutSection
import now.shouldigooutside.settings.ui.section.ExperienceSection
import now.shouldigooutside.settings.ui.section.ThemeSection
import now.shouldigooutside.settings.ui.section.VersionSection
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsScreen(
    onBack: () -> Unit,
    toInternalSettings: () -> Unit,
    toWebView: (title: String, url: String) -> Unit,
    toUnits: () -> Unit,
    model: SettingsModel = koinViewModel(),
) {
    HandleEvents(model) { event ->
        when (event) {
            is SettingsModel.Event.OpenWebView -> toWebView(event.title, event.url)
        }
    }

    val state by model.collectAsState()

    val snackbar = rememberSnackbarProvider()
    SettingsScreen(
        settings = state.settings,
        snackbarHostState = snackbar.hostState,
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is SettingsAction.Close -> {
                    onBack()
                }
                is SettingsAction.UpdateTheme -> {
                    model.updateTheme(action.mode)
                }
                is SettingsAction.ToggleHaptics -> {
                    model.toggleHaptics()
                }
                is SettingsAction.Toggle24HourFormat -> {
                    model.toggle24HourFormat()
                }
                is SettingsAction.ToggleAirQuality -> {
                    model.toggleAirQuality()
                }
                is SettingsAction.ToggleActivities -> {
                    model.toggleActivities()
                }
                is SettingsAction.ToggleRememberActivity -> {
                    model.toggleRememberActivity()
                }
                is SettingsAction.UpdateWidgetActivity -> {
                    model.updateWidgetActivity(action.activity)
                }
                is SettingsAction.ToUnitsScreen -> {
                    toUnits()
                }
                is SettingsAction.RateApp -> {
                    if (state.canEnableInternalSettings) {
                        model.enableInternalSettings()
                    } else {
                        launchAppStore()
                    }
                }
                is SettingsAction.ShareApp -> {
                    shareApp()
                }
                is SettingsAction.TapAbout -> {
                    model.clickAbout()
                }
                is SettingsAction.TapVersion -> {
                    if (!state.settings.internalSettings.enabled) {
                        model.clickVersion()
                    } else {
                        toInternalSettings()
                    }
                }
                is SettingsAction.WebViewUrl -> {
                    model.handleWebViewUrl(action)
                }
            }
        },
    )
}

@Composable
internal fun SettingsScreen(
    settings: Settings,
    dispatcher: Dispatcher<SettingsAction>,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    version: Version = Version,
) {
    val scrollBehavior = TopBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier,
        containerColor = AppTheme.colors.surface,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                },
            )
        },
        topBar = {
            SettingsTopBar(
                scrollBehavior = scrollBehavior,
                text = Res.string.settings,
                onBack = dispatcher.rememberRelay(SettingsAction.Close),
            )
        },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .innerPadding(innerPadding, bottom = false)
                .padding(horizontal = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(8.dp))

            ThemeSection(
                selected = settings.themeMode,
                updateTheme = dispatcher.rememberRelayOf(SettingsAction::UpdateTheme),
                primary = AppTheme.colors.primary,
                secondary = AppTheme.colors.quaternary,
            )

            ExperienceSection(
                settings = settings,
                toggleHaptics = dispatcher.rememberRelay(SettingsAction.ToggleHaptics),
                toggle24HourFormat = dispatcher.rememberRelay(SettingsAction.Toggle24HourFormat),
                toggleAirQuality = dispatcher.rememberRelay(SettingsAction.ToggleAirQuality),
                toggleActivities = dispatcher.rememberRelay(SettingsAction.ToggleActivities),
                toggleRememberActivity = dispatcher.rememberRelay(SettingsAction.ToggleRememberActivity),
                updateWidgetActivity = dispatcher.rememberRelayOf(SettingsAction::UpdateWidgetActivity),
                unitsClick = dispatcher.rememberRelay(SettingsAction.ToUnitsScreen),
            )

            AboutSection(
                dispatcher = dispatcher,
                primary = AppTheme.colors.tertiary,
                secondary = AppTheme.colors.secondary,
            )

            VersionSection(
                internalSettingsEnabled = settings.internalSettings.enabled,
                onClick = dispatcher.rememberRelay(SettingsAction.TapVersion),
                version = version,
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
                    .navigationBarsPadding(),
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    AppPreview {
        SettingsScreen(
            settings = Settings(),
            dispatcher = rememberDebounceDispatcher {},
        )
    }
}
