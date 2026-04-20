package now.shouldigooutside.core.ui.preferences

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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.PrecipitationUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_precipitation_description
import now.shouldigooutside.core.resources.preferences_precipitation_title
import now.shouldigooutside.core.resources.rain
import now.shouldigooutside.core.resources.snow
import now.shouldigooutside.core.ui.LocalAppExperience
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Droplet
import now.shouldigooutside.core.ui.icons.lucide.Snowflake
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
public fun PreferencesList(
    units: Units,
    preferences: Preferences,
    updatePreferences: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    var showAqiInfo by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        TemperatureRange(
            units = units,
            preferences = preferences,
            update = updatePreferences,
            temperatureRange = temperatureRange,
        )

        WindRange(
            units = units,
            preferences = preferences,
            update = updatePreferences,
            maxWindSpeed = maxWindSpeed,
        )

        if (LocalAppExperience.current.includeAirQuality) {
            AqiRange(
                preferences = preferences,
                update = updatePreferences,
                onInfoClick = { showAqiInfo = true },
            )
        }

        PreferenceCard(
            title = Res.string.preferences_precipitation_title,
            description = Res.string.preferences_precipitation_description,
            icon = AppIcons.Lucide.Droplet,
            colors = PrecipitationUnit.colors(),
            enabled = preferences.precipitationEnabled,
            onEnabledChange = { updatePreferences(preferences.copy(precipitationEnabled = it)) },
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp),
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

    AqiInfoSheet(
        isVisible = showAqiInfo,
        onDismiss = { showAqiInfo = false },
    )
}

@PreviewLightDark
@Composable
private fun PreferencesListPreview() {
    var preferences by remember { mutableStateOf(Preferences.default) }
    AppPreview {
        Column(modifier = Modifier.padding(16.dp)) {
            PreferencesList(
                units = Units.Metric,
                preferences = preferences,
                updatePreferences = { preferences = it },
            )
        }
    }
}
