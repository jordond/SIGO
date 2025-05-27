package app.sigot.core.ui.preferences

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
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.resources.Res
import app.sigot.core.resources.preferences_temp_description
import app.sigot.core.resources.unit_temperature
import app.sigot.core.resources.unit_wind
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.asContent
import app.sigot.core.ui.cardColors
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.RangeSlider
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.mappers.units.colors
import app.sigot.core.ui.mappers.units.icon
import app.sigot.core.ui.mappers.units.maxTemperatureString
import app.sigot.core.ui.mappers.units.minTemperatureString
import app.sigot.core.ui.sliderColors

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
