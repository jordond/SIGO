package now.shouldigooutside.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.aqi_info_good_description
import now.shouldigooutside.core.resources.aqi_info_good_range
import now.shouldigooutside.core.resources.aqi_info_good_title
import now.shouldigooutside.core.resources.aqi_info_hazardous_description
import now.shouldigooutside.core.resources.aqi_info_hazardous_range
import now.shouldigooutside.core.resources.aqi_info_hazardous_title
import now.shouldigooutside.core.resources.aqi_info_moderate_description
import now.shouldigooutside.core.resources.aqi_info_moderate_range
import now.shouldigooutside.core.resources.aqi_info_moderate_title
import now.shouldigooutside.core.resources.aqi_info_sensitive_description
import now.shouldigooutside.core.resources.aqi_info_sensitive_range
import now.shouldigooutside.core.resources.aqi_info_sensitive_title
import now.shouldigooutside.core.resources.aqi_info_unhealthy_description
import now.shouldigooutside.core.resources.aqi_info_unhealthy_range
import now.shouldigooutside.core.resources.aqi_info_unhealthy_title
import now.shouldigooutside.core.resources.aqi_info_very_unhealthy_description
import now.shouldigooutside.core.resources.aqi_info_very_unhealthy_range
import now.shouldigooutside.core.resources.aqi_info_very_unhealthy_title
import org.jetbrains.compose.resources.StringResource

@Stable
public data class AqiLevel(
    val colors: BrutalColors,
    val title: StringResource,
    val range: StringResource,
    val description: StringResource,
)

public object AqiLevels {
    @Composable
    public fun all(): List<AqiLevel> =
        listOf(
            AqiLevel(
                colors = AppTheme.colors.brutal.green,
                title = Res.string.aqi_info_good_title,
                range = Res.string.aqi_info_good_range,
                description = Res.string.aqi_info_good_description,
            ),
            AqiLevel(
                colors = AppTheme.colors.brutal.yellow,
                title = Res.string.aqi_info_moderate_title,
                range = Res.string.aqi_info_moderate_range,
                description = Res.string.aqi_info_moderate_description,
            ),
            AqiLevel(
                colors = AppTheme.colors.brutal.orange,
                title = Res.string.aqi_info_sensitive_title,
                range = Res.string.aqi_info_sensitive_range,
                description = Res.string.aqi_info_sensitive_description,
            ),
            AqiLevel(
                colors = AppTheme.colors.brutal.vermilion,
                title = Res.string.aqi_info_unhealthy_title,
                range = Res.string.aqi_info_unhealthy_range,
                description = Res.string.aqi_info_unhealthy_description,
            ),
            AqiLevel(
                colors = AppTheme.colors.brutal.red,
                title = Res.string.aqi_info_very_unhealthy_title,
                range = Res.string.aqi_info_very_unhealthy_range,
                description = Res.string.aqi_info_very_unhealthy_description,
            ),
            AqiLevel(
                colors = AppTheme.colors.brutal.maroon,
                title = Res.string.aqi_info_hazardous_title,
                range = Res.string.aqi_info_hazardous_range,
                description = Res.string.aqi_info_hazardous_description,
            ),
        )

    /**
     * Returns the [AqiLevel] for the given normalized AQI value (1-11 scale).
     * Returns the first level (Good) for 0 (no data).
     */
    @Composable
    public fun forValue(aqi: AirQuality): AqiLevel {
        val levels = all()
        return when {
            aqi <= 2 -> levels[0]
            aqi <= 4 -> levels[1]
            aqi <= 6 -> levels[2]
            aqi <= 8 -> levels[3]
            aqi <= 10 -> levels[4]
            else -> levels[5]
        }
    }
}
