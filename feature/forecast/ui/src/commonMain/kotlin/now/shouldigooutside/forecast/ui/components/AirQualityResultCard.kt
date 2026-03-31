package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.aqi_not_available
import now.shouldigooutside.core.resources.aqi_value
import now.shouldigooutside.core.resources.unit_air_quality
import now.shouldigooutside.core.ui.AqiLevels
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Info
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preferences.AqiInfoSheet
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.forecast.ui.components.mappers.aqiColors

@Composable
internal fun AirQualityResultCard(
    airQuality: AirQuality,
    modifier: Modifier = Modifier,
    title: String = Res.string.unit_air_quality.get(),
    text: String = AqiLevels.forValue(airQuality).title.get(),
) {
    var showAqiInfo by remember { mutableStateOf(false) }
    PreferenceResultCard(
        title = title,
        text = text,
        colors = aqiColors(airQuality),
        icon = AppIcons.Lucide.Info,
        value = {
            if (!airQuality.hasData) {
                Res.string.aqi_not_available.get()
            } else {
                Res.string.aqi_value.get(airQuality.value)
            }
        },
        modifier = modifier
            .clickable { showAqiInfo = true },
    )

    AqiInfoSheet(
        isVisible = showAqiInfo,
        onDismiss = { showAqiInfo = false },
    )
}

private class Params : PreviewParameterProvider<AirQuality> {
    override val values: Sequence<AirQuality>
        get() = sequenceOf(
            AirQuality(2),
            AirQuality(4),
            AirQuality(6),
            AirQuality(8),
            AirQuality(10),
            AirQuality(11),
        )
}

@Preview
@Composable
private fun Preview(
    @PreviewParameter(Params::class) airQuality: AirQuality,
) {
    AppPreview {
        AirQualityResultCard(airQuality = airQuality)
    }
}
