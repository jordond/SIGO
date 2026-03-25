package now.shouldigooutside.settings.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.onboarding_units
import now.shouldigooutside.core.resources.preferences
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Scaffold
import now.shouldigooutside.core.ui.preferences.PreferencesList
import now.shouldigooutside.settings.ui.components.SettingsTopBar
import now.shouldigooutside.settings.ui.components.SettingsTopBarNav
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PreferencesScreen(
    onBack: () -> Unit,
    model: PreferencesModel = koinViewModel(),
) {
    val state by model.collectAsState()

    PreferencesScreen(
        units = state.units,
        preferences = state.preferences,
        update = model::update,
        onBack = onBack,
        temperatureRange = state.tempRange,
        maxWindSpeed = state.maxWindSpeed,
    )
}

@Composable
internal fun PreferencesBottomSheet(
    onBack: () -> Unit,
    model: PreferencesModel = koinViewModel(),
) {
    val state by model.collectAsState()

    PreferencesBottomSheet(
        units = state.units,
        preferences = state.preferences,
        update = model::update,
        onBack = onBack,
        temperatureRange = state.tempRange,
        maxWindSpeed = state.maxWindSpeed,
    )
}

@Composable
internal fun PreferencesBottomSheet(
    units: Units,
    preferences: Preferences,
    update: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        SettingsTopBar(
            text = Res.string.preferences,
            onBack = onBack,
            navType = SettingsTopBarNav.Close,
            handleInsets = false,
        )

        PreferencesList(
            units = units,
            preferences = preferences,
            updatePreferences = update,
            temperatureRange = temperatureRange,
            maxWindSpeed = maxWindSpeed,
            modifier = Modifier.padding(bottom = 64.dp),
        )
    }
}

@Composable
internal fun PreferencesScreen(
    units: Units,
    preferences: Preferences,
    update: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    Scaffold(
        modifier = modifier,
        containerColor = AppTheme.colors.surface,
        topBar = {
            SettingsTopBar(
                text = Res.string.onboarding_units,
                onBack = onBack,
                navType = SettingsTopBarNav.Back,
            )
        },
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                ).padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            PreferencesList(
                units = units,
                preferences = preferences,
                updatePreferences = update,
                temperatureRange = temperatureRange,
                maxWindSpeed = maxWindSpeed,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun ScreenPreview() {
    var preferences by remember { mutableStateOf(Preferences.default) }
    PreferencesScreen(
        units = Units.Metric,
        preferences = preferences,
        update = { preferences = it },
        onBack = {},
    )
}
