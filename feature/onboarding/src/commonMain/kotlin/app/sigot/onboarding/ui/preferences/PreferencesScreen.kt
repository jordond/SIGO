package app.sigot.onboarding.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.PrecipitationUnit
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.model.units.WindSpeedUnit
import app.sigot.core.resources.Res
import app.sigot.core.resources.onboarding_preferences
import app.sigot.core.resources.onboarding_preferences_subtext
import app.sigot.core.resources.preferences_precipitation_title
import app.sigot.core.resources.preferences_temp_description
import app.sigot.core.resources.preferences_wind_description
import app.sigot.core.resources.rain
import app.sigot.core.resources.snow
import app.sigot.core.resources.unit_temperature
import app.sigot.core.resources.unit_wind
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.asContent
import app.sigot.core.ui.cardColors
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.RangeSlider
import app.sigot.core.ui.components.Slider
import app.sigot.core.ui.components.Switch
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.autoSize
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Droplet
import app.sigot.core.ui.icons.lucide.Snowflake
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.mappers.units.colors
import app.sigot.core.ui.mappers.units.icon
import app.sigot.core.ui.mappers.units.maxTemperatureString
import app.sigot.core.ui.mappers.units.minTemperatureString
import app.sigot.core.ui.mappers.units.windSpeedString
import app.sigot.core.ui.sliderColors
import app.sigot.core.ui.switchColors
import app.sigot.onboarding.ui.OnboardingScreenPreview
import app.sigot.onboarding.ui.navigation.OnboardingDestination
import dev.stateholder.extensions.collectAsState
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PreferencesScreen(model: PreferencesModel = koinViewModel()) {
    val state by model.collectAsState()

    PreferencesScreen(
        preferences = state.preferences,
        updatePreferences = model::update,
        temperatureRange = state.tempRange,
        maxWindSpeed = state.maxWindSpeed,
    )
}

@Composable
internal fun PreferencesScreen(
    preferences: Preferences,
    updatePreferences: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    temperatureRange: ClosedFloatingPointRange<Float> = -30f..30f,
    maxWindSpeed: Float = 40f,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text = Res.string.onboarding_preferences,
                    style = AppTheme.typography.header,
                    autoSize = AppTheme.typography.header.autoSize(),
                    maxLines = 1,
                )

                Text(
                    text = Res.string.onboarding_preferences_subtext,
                    modifier = Modifier.padding(start = 8.dp),
                    style = AppTheme.typography.body1,
                )
            }

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
}

@Composable
internal fun TemperatureRange(
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

@Composable
internal fun WindRange(
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

@Composable
internal fun PrecipitationToggle(
    text: StringResource,
    icon: ImageVector,
    checked: Boolean,
    update: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = PrecipitationUnit.colors()
    ElevatedCard(
        colors = colors.cardColors(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
        ) {
            Icon(
                icon = icon,
                contentDescription = text.get(),
            )
            Text(
                text = text,
                style = AppTheme.typography.h4,
            )
            Spacer(Modifier.width(16.dp))
            Switch(
                checked = checked,
                onCheckedChange = update,
                colors = colors.switchColors(),
            )
        }
    }
}

@Preview
@Composable
private fun PreferencesScreenPreview() {
    OnboardingScreenPreview(OnboardingDestination.Preferences)
}
