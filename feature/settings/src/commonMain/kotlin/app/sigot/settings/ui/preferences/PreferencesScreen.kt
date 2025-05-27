package app.sigot.settings.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.resources.Res
import app.sigot.core.resources.preferences
import app.sigot.core.ui.preferences.PreferencesList
import app.sigot.core.ui.preview.AppPreview
import app.sigot.settings.ui.components.SettingsTopBar
import app.sigot.settings.ui.components.SettingsTopBarNav
import dev.stateholder.extensions.collectAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PreferencesScreen(
    onBack: () -> Unit,
    isBack: Boolean,
    model: PreferencesModel = koinViewModel(),
) {
    val state by model.collectAsState()

    PreferencesScreen(
        preferences = state.preferences,
        update = model::update,
        onBack = onBack,
        isBack = isBack,
        temperatureRange = state.tempRange,
        maxWindSpeed = state.maxWindSpeed,
    )
}

@Composable
internal fun PreferencesScreen(
    preferences: Preferences,
    update: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    isBack: Boolean = false,
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        val type = remember(isBack) {
            if (isBack) SettingsTopBarNav.Back else SettingsTopBarNav.Close
        }
        SettingsTopBar(
            text = Res.string.preferences,
            onBack = onBack,
            navType = type,
            handleInsets = false,
        )

        PreferencesList(
            preferences = preferences,
            updatePreferences = update,
            temperatureRange = temperatureRange,
            maxWindSpeed = maxWindSpeed,
            modifier = Modifier.padding(bottom = 64.dp),
        )
    }
}

@Composable
private fun ScreenPreview() {
    var preferences by remember { mutableStateOf(Preferences.default) }
    PreferencesScreen(
        preferences = preferences,
        update = { preferences = it },
        onBack = {},
        isBack = false,
    )
}

@Preview
@Composable
private fun UnitsScreenPreview() {
    AppPreview {
        ScreenPreview()
    }
}

@Preview
@Composable
private fun UnitsScreenDarkPreview() {
    AppPreview(isDarkTheme = true) {
        ScreenPreview()
    }
}
