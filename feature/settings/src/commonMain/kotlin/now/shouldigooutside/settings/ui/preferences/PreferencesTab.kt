package now.shouldigooutside.settings.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.ui.preferences.PreferencesList
import now.shouldigooutside.settings.ui.navigation.PreferencesTabRoute
import org.koin.compose.viewmodel.koinViewModel

public fun NavGraphBuilder.preferencesTab() {
    composable<PreferencesTabRoute> {
        PreferencesTab()
    }
}

@Composable
internal fun PreferencesTab(model: PreferencesModel = koinViewModel()) {
    val state by model.collectAsState()

    PreferencesTab(
        units = state.units,
        preferences = state.preferences,
        update = model::update,
        temperatureRange = state.tempRange,
        maxWindSpeed = state.maxWindSpeed,
    )
}

@Composable
public fun PreferencesTab(
    units: Units,
    preferences: Preferences,
    update: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    Column(
        modifier = modifier,
    ) {
        PreferencesList(
            units = units,
            preferences = preferences,
            updatePreferences = update,
            temperatureRange = temperatureRange,
            maxWindSpeed = maxWindSpeed,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    var preferences by remember { mutableStateOf(Preferences.default) }
    PreferencesScreen(
        units = Units.Metric,
        preferences = preferences,
        update = { preferences = it },
        onBack = {},
    )
}
