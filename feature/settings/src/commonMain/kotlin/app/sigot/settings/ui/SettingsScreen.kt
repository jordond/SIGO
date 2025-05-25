package app.sigot.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import app.sigot.core.Version
import app.sigot.core.model.settings.Settings
import app.sigot.core.platform.launchAppStore
import app.sigot.core.resources.Res
import app.sigot.core.resources.settings
import app.sigot.core.resources.settings_about_version
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.snackbar.Snackbar
import app.sigot.core.ui.components.snackbar.SnackbarHost
import app.sigot.core.ui.components.snackbar.SnackbarHostState
import app.sigot.core.ui.components.snackbar.rememberSnackbarProvider
import app.sigot.core.ui.ktx.clickableWithoutRipple
import app.sigot.core.ui.preview.AppPreview
import app.sigot.settings.ui.components.SettingsTopBar
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsScreen(
    onBack: () -> Unit,
    toInternalSettings: () -> Unit,
    toWebView: (title: String, url: String) -> Unit,
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
                is SettingsAction.Close -> onBack()
                is SettingsAction.RateApp -> {
                    if (state.canEnableInternalSettings) {
                        model.enableInternalSettings()
                    } else {
                        launchAppStore()
                    }
                }
                is SettingsAction.ShareApp -> {
                    // TODO: Share app
                }
                is SettingsAction.TapAbout -> model.clickAbout()
                is SettingsAction.TapVersion -> {
                    if (!state.settings.internalSettings.enabled) {
                        model.clickVersion()
                    } else {
                        toInternalSettings()
                    }
                }
                is SettingsAction.WebViewUrl -> model.handleWebViewUrl(action)
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
    Scaffold(
        modifier = modifier,
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
                text = Res.string.settings,
                onBack = dispatcher.rememberRelay(SettingsAction.Close),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            Text(
                text = stringResource(
                    Res.string.settings_about_version,
                    version.NAME,
                    version.CODE,
                    version.GIT_SHA,
                ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontStyle = FontStyle.Italic,
                textDecoration = if (settings.internalSettings.enabled) {
                    TextDecoration.Underline
                } else {
                    TextDecoration.None
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableWithoutRipple(dispatcher.rememberRelay(SettingsAction.TapVersion))
                    .padding(bottom = 32.dp),
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
