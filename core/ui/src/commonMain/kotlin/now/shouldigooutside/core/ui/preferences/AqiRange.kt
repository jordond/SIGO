package now.shouldigooutside.core.ui.preferences

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_aqi_description
import now.shouldigooutside.core.resources.preferences_aqi_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.AqiLevels
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.IconButton
import now.shouldigooutside.core.ui.components.IconButtonVariant
import now.shouldigooutside.core.ui.components.Slider
import now.shouldigooutside.core.ui.components.SliderDefaults
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Info
import now.shouldigooutside.core.ui.icons.lucide.Waves
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

private val AqiSliderRange = 1f..11f

@Composable
public fun AqiRange(
    preferences: Preferences,
    update: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    onInfoClick: () -> Unit = {},
) {
    val levels = AqiLevels.forValue(preferences.maxAqi)
    val targetColors = levels.colors
    val bright by animateColorAsState(targetColors.bright)
    val container by animateColorAsState(targetColors.container)
    val contentColor by animateColorAsState(targetColors.containerContent)
    val sliderActive by animateColorAsState(targetColors.bright)
    val sliderInactive by animateColorAsState(targetColors.low)

    val animatedColors = remember(bright, container, contentColor, targetColors) {
        BrutalColors(
            bright = bright,
            onBright = targetColors.onBright,
            high = targetColors.high,
            onHigh = targetColors.onHigh,
            normal = container,
            onNormal = contentColor,
            low = targetColors.low,
            onLow = targetColors.onLow,
            lowest = targetColors.lowest,
            onLowest = targetColors.onLowest,
        )
    }

    PreferenceCard(
        title = Res.string.preferences_aqi_title,
        description = Res.string.preferences_aqi_description,
        icon = AppIcons.Lucide.Waves,
        colors = animatedColors,
        enabled = preferences.aqiEnabled,
        onEnabledChange = { update(preferences.copy(aqiEnabled = it)) },
        modifier = modifier,
        trailing = {
            IconButton(
                onClick = onInfoClick,
                variant = IconButtonVariant.Ghost,
            ) {
                Icon(
                    icon = AppIcons.Lucide.Info,
                    contentDescription = Res.string.preferences_aqi_title.get(),
                )
            }
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 32.dp, end = 16.dp),
        ) {
            Text(
                text = preferences.maxAqi.value.toString(),
                style = AppTheme.typography.h1,
            )

            Crossfade(
                targetState = levels,
                modifier = Modifier.padding(start = 16.dp),
            ) { aqi ->
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(
                        text = aqi.title,
                        style = AppTheme.typography.h4,
                        maxLines = 1,
                        autoSize = AppTheme.typography.h4.autoSize(),
                    )
                    Text(
                        text = aqi.description,
                        maxLines = 3,
                        minLines = 3,
                        autoSize = AppTheme.typography.body3.autoSize(),
                        style = AppTheme.typography.body3,
                    )
                }
            }
        }

        Slider(
            value = preferences.maxAqi.value.toFloat(),
            onValueChange = { update(preferences.copy(maxAqi = AirQuality(it.toInt()))) },
            valueRange = AqiSliderRange,
            steps = 9,
            colors = SliderDefaults.colors(
                activeTrackColor = sliderActive,
                inactiveTrackColor = sliderInactive,
            ),
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
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            var preferences by remember { mutableStateOf(Preferences.default) }
            AqiRange(
                preferences = preferences,
                update = { preferences = it },
            )
        }
    }
}
