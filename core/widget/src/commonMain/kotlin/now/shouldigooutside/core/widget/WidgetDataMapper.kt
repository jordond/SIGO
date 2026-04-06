package now.shouldigooutside.core.widget

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.activity_title_custom
import now.shouldigooutside.core.resources.activity_title_cycling
import now.shouldigooutside.core.resources.activity_title_general
import now.shouldigooutside.core.resources.activity_title_hiking
import now.shouldigooutside.core.resources.activity_title_running
import now.shouldigooutside.core.resources.activity_title_swimming
import now.shouldigooutside.core.resources.activity_title_walking
import now.shouldigooutside.core.resources.unit_temperature_celsius
import now.shouldigooutside.core.resources.unit_temperature_fahrenheit
import now.shouldigooutside.core.resources.unit_temperature_kelvin
import now.shouldigooutside.core.resources.unit_wind_knots
import now.shouldigooutside.core.resources.unit_wind_kph
import now.shouldigooutside.core.resources.unit_wind_mph
import now.shouldigooutside.core.resources.unit_wind_ms
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.math.roundToInt
import kotlin.time.Clock

public object WidgetDataMapper {
    public suspend fun map(
        forecast: Forecast,
        score: ForecastScore,
        units: Units,
        activity: Activity,
    ): WidgetData {
        val tempLabel = getString(units.temperature.unitResource())
        val windLabel = getString(units.windSpeed.unitResource())
        val activityName = when (activity) {
            is Activity.General -> null
            else -> getString(activity.titleResource())
        }
        return WidgetData(
            scoreResult = score.current.result,
            locationName = forecast.location.name,
            formattedTemp = "${forecast.current.temperature.value.roundToInt()}$tempLabel",
            formattedFeelsLike = "${forecast.current.temperature.feelsLike.roundToInt()}$tempLabel",
            formattedWind = "${forecast.current.wind.speed.roundToInt()} $windLabel",
            precipChance = forecast.current.precipitation.probability,
            todayScoreResult = score.today.result,
            alertCount = forecast.alerts.size,
            updatedAtMillis = Clock.System.now().toEpochMilliseconds(),
            activityName = activityName,
        )
    }
}

private fun Activity.titleResource(): StringResource =
    when (this) {
        is Activity.Custom -> Res.string.activity_title_custom
        is Activity.Cycling -> Res.string.activity_title_cycling
        is Activity.General -> Res.string.activity_title_general
        is Activity.Hiking -> Res.string.activity_title_hiking
        is Activity.Running -> Res.string.activity_title_running
        is Activity.Swimming -> Res.string.activity_title_swimming
        is Activity.Walking -> Res.string.activity_title_walking
    }

private fun TemperatureUnit.unitResource(): StringResource =
    when (this) {
        TemperatureUnit.Kelvin -> Res.string.unit_temperature_kelvin
        TemperatureUnit.Celsius -> Res.string.unit_temperature_celsius
        TemperatureUnit.Fahrenheit -> Res.string.unit_temperature_fahrenheit
    }

private fun WindSpeedUnit.unitResource(): StringResource =
    when (this) {
        WindSpeedUnit.MeterPerSecond -> Res.string.unit_wind_ms
        WindSpeedUnit.KilometerPerHour -> Res.string.unit_wind_kph
        WindSpeedUnit.MilePerHour -> Res.string.unit_wind_mph
        WindSpeedUnit.Knot -> Res.string.unit_wind_knots
    }
