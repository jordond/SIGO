package now.shouldigooutside.core.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_wind_description
import now.shouldigooutside.core.resources.preferences_wind_max
import now.shouldigooutside.core.resources.unit_wind
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Slider
import now.shouldigooutside.core.ui.components.SliderDefaults
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.mappers.units.icon
import now.shouldigooutside.core.ui.mappers.units.maxWindSpeedString
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.sliderColors

@Composable
public fun WindRange(
    units: Units,
    preferences: Preferences,
    update: (Preferences) -> Unit,
    maxWindSpeed: Float,
    modifier: Modifier = Modifier,
) {
    val colors = WindSpeedUnit.colors()
    PreferenceCard(
        title = Res.string.unit_wind,
        description = Res.string.preferences_wind_description,
        icon = remember { WindSpeedUnit.icon() },
        colors = colors,
        enabled = preferences.windEnabled,
        onEnabledChange = { update(preferences.copy(windEnabled = it)) },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 32.dp),
        ) {
            Text(
                text = Res.string.preferences_wind_max,
                style = AppTheme.typography.h4,
                fontWeight = FontWeight.Light,
            )

            Text(
                text = preferences.maxWindSpeedString(units.windSpeed),
                style = AppTheme.typography.h1,
            )
        }

        Slider(
            value = preferences.windSpeed.toFloat(),
            onValueChange = { update(preferences.copy(windSpeed = it.toInt())) },
            valueRange = remember(maxWindSpeed) { 0f..maxWindSpeed },
            colors = colors.sliderColors(),
            tickLabel = { SliderDefaults.TickLabel(it) },
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 16.dp),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        var preferences by remember { mutableStateOf(Preferences.default) }
        WindRange(
            units = Units.Metric,
            preferences = preferences,
            update = { preferences = it },
            maxWindSpeed = 40f,
        )
    }
}
