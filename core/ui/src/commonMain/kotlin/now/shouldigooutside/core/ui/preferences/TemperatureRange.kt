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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_temp_description
import now.shouldigooutside.core.resources.unit_temperature
import now.shouldigooutside.core.resources.unit_wind
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.asContent
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.RangeSlider
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.mappers.units.icon
import now.shouldigooutside.core.ui.mappers.units.maxTemperatureString
import now.shouldigooutside.core.ui.mappers.units.minTemperatureString
import now.shouldigooutside.core.ui.sliderColors

@Composable
public fun TemperatureRange(
    preferences: Preferences,
    update: (Preferences) -> Unit,
    temperatureRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
) {
    val colors = TemperatureUnit.colors()
    ElevatedCard(
        colors = colors.cardColors(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    icon = remember { TemperatureUnit.icon() },
                    contentDescription = Res.string.unit_wind.get(),
                )

                Text(
                    text = Res.string.unit_temperature,
                    style = AppTheme.typography.h3.asContent,
                )
            }

            Text(
                text = Res.string.preferences_temp_description,
            )

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
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = preferences.minTemperatureString(),
                    style = AppTheme.typography.h4,
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = preferences.maxTemperatureString(),
                    style = AppTheme.typography.h4,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}
