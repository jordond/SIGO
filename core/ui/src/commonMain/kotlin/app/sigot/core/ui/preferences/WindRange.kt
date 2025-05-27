package app.sigot.core.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.WindSpeedUnit
import app.sigot.core.resources.Res
import app.sigot.core.resources.preferences_wind_description
import app.sigot.core.resources.unit_wind
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.asContent
import app.sigot.core.ui.cardColors
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Slider
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.mappers.units.colors
import app.sigot.core.ui.mappers.units.icon
import app.sigot.core.ui.mappers.units.windSpeedString
import app.sigot.core.ui.sliderColors

@Composable
public fun WindRange(
    preferences: Preferences,
    update: (Preferences) -> Unit,
    maxWindSpeed: Float,
    modifier: Modifier = Modifier,
) {
    val colors = WindSpeedUnit.colors()
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
                    icon = remember { WindSpeedUnit.icon() },
                    contentDescription = Res.string.unit_wind.get(),
                )

                Text(
                    text = Res.string.unit_wind,
                    style = AppTheme.typography.h3.asContent,
                )
            }

            Text(
                text = Res.string.preferences_wind_description,
            )

            Slider(
                value = preferences.windSpeed.toFloat(),
                onValueChange = { update(preferences.copy(windSpeed = it.toInt())) },
                valueRange = remember(maxWindSpeed) { 0f..maxWindSpeed },
                colors = colors.sliderColors(),
            )

            Text(
                text = preferences.windSpeedString(),
                style = AppTheme.typography.h4,
            )
        }
    }
}
