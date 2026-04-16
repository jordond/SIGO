package now.shouldigooutside.core.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_temp_description
import now.shouldigooutside.core.resources.preferences_temp_max
import now.shouldigooutside.core.resources.preferences_temp_min
import now.shouldigooutside.core.resources.unit_temperature
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.RangeSlider
import now.shouldigooutside.core.ui.components.SliderDefaults
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.mappers.units.icon
import now.shouldigooutside.core.ui.mappers.units.maxTemperatureString
import now.shouldigooutside.core.ui.mappers.units.minTemperatureString
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.sliderColors

@Composable
public fun TemperatureRange(
    units: Units,
    preferences: Preferences,
    update: (Preferences) -> Unit,
    temperatureRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
) {
    val colors = TemperatureUnit.colors()
    PreferenceCard(
        title = Res.string.unit_temperature,
        description = Res.string.preferences_temp_description,
        icon = remember { TemperatureUnit.icon() },
        colors = colors,
        enabled = preferences.temperatureEnabled,
        onEnabledChange = { update(preferences.copy(temperatureEnabled = it)) },
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 32.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = Res.string.preferences_temp_min,
                    style = AppTheme.typography.h4,
                    fontWeight = FontWeight.Light,
                )

                Text(
                    text = preferences.minTemperatureString(units.temperature),
                    style = AppTheme.typography.h1,
                )
            }

            Spacer(Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = Res.string.preferences_temp_max,
                    style = AppTheme.typography.h4,
                    fontWeight = FontWeight.Light,
                )

                Text(
                    text = preferences.maxTemperatureString(units.temperature),
                    style = AppTheme.typography.h1,
                )
            }
        }

        val range = remember(preferences.minTemperature, preferences.maxTemperature) {
            preferences.minTemperature.toFloat()..preferences.maxTemperature.toFloat()
        }
        RangeSlider(
            value = range,
            onValueChange = { newRange ->
                val prefs = preferences.copy(
                    minTemperature = newRange.start.toInt(),
                    maxTemperature = newRange.endInclusive.toInt(),
                )
                update(prefs)
            },
            valueRange = temperatureRange,
            colors = colors.sliderColors(),
            tickLabel = { SliderDefaults.TickLabel(it) },
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 16.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    AppPreview {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TemperatureRange(
                units = Units.Metric,
                preferences = Preferences.default,
                update = {},
                temperatureRange = -20f..50f,
            )
            TemperatureRange(
                units = Units.Metric,
                preferences = Preferences.default.copy(temperatureEnabled = false),
                update = {},
                temperatureRange = -20f..50f,
            )
        }
    }
}
