package now.shouldigooutside.core.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_wind_description
import now.shouldigooutside.core.resources.unit_wind
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.asContent
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Slider
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.mappers.units.icon
import now.shouldigooutside.core.ui.mappers.units.windSpeedString
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
                text = preferences.windSpeedString(units.windSpeed),
                style = AppTheme.typography.h4,
            )
        }
    }
}
