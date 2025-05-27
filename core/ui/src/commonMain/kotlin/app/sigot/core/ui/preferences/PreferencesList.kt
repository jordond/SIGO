package app.sigot.core.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.resources.Res
import app.sigot.core.resources.preferences_precipitation_title
import app.sigot.core.resources.rain
import app.sigot.core.resources.snow
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Droplet
import app.sigot.core.ui.icons.lucide.Snowflake
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun PreferencesList(
    preferences: Preferences,
    updatePreferences: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        TemperatureRange(
            preferences = preferences,
            update = updatePreferences,
            temperatureRange = temperatureRange,
        )

        WindRange(
            preferences = preferences,
            update = updatePreferences,
            maxWindSpeed = maxWindSpeed,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(
                text = Res.string.preferences_precipitation_title,
                style = AppTheme.typography.body1,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PrecipitationToggle(
                    text = Res.string.rain,
                    icon = AppIcons.Lucide.Droplet,
                    checked = preferences.rain,
                    update = { updatePreferences(preferences.copy(rain = it)) },
                )

                PrecipitationToggle(
                    text = Res.string.snow,
                    icon = AppIcons.Lucide.Snowflake,
                    checked = preferences.snow,
                    update = { updatePreferences(preferences.copy(snow = it)) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreferencesListPreview() {
    var preferences by remember { mutableStateOf(Preferences.default) }
    AppPreview {
        Column(modifier = Modifier.padding(16.dp)) {
            PreferencesList(
                preferences = preferences,
                updatePreferences = { preferences = it },
            )
        }
    }
}
