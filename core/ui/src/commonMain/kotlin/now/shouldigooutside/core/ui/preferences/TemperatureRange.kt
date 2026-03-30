package now.shouldigooutside.core.ui.preferences

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_temp_description
import now.shouldigooutside.core.resources.preferences_temp_max
import now.shouldigooutside.core.resources.preferences_temp_min
import now.shouldigooutside.core.resources.unit_temperature
import now.shouldigooutside.core.resources.unit_wind
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalTextStyle
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.RangeSlider
import now.shouldigooutside.core.ui.components.SliderDefaults
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.ktx.get
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
    ElevatedCard(
        colors = colors.cardColors(),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(colors.bright)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = Res.string.unit_temperature,
                        style = AppTheme.typography.h3,
                    )

                    Text(
                        text = Res.string.preferences_temp_description,
                        autoSize = LocalTextStyle.current.autoSize(),
                        maxLines = 1,
                    )
                }

                Icon(
                    icon = remember { TemperatureUnit.icon() },
                    contentDescription = Res.string.unit_wind.get(),
                )
            }

            HorizontalDivider()

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
                onValueChange = { range ->
                    val prefs = preferences.copy(
                        minTemperature = range.start.toInt(),
                        maxTemperature = range.endInclusive.toInt(),
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
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        TemperatureRange(
            units = Units.Metric,
            preferences = Preferences.default,
            update = {},
            temperatureRange = -20f..50f,
        )
    }
}
