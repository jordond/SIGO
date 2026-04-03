package now.shouldigooutside.core.widget

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Clock

public fun Activity.widgetDisplayName(): String =
    when (this) {
        is Activity.General -> WidgetData.DEFAULT_ACTIVITY_NAME
        is Activity.Walking -> "Walking"
        is Activity.Running -> "Running"
        is Activity.Cycling -> "Cycling"
        is Activity.Hiking -> "Hiking"
        is Activity.Swimming -> "Swimming"
        is Activity.Custom -> name
    }

public object WidgetDataMapper {
    public fun map(
        forecast: Forecast,
        score: ForecastScore,
        units: Units,
        activityName: String,
    ): WidgetData =
        WidgetData(
            scoreResult = score.current.result.name,
            locationName = forecast.location.name,
            currentTemp = forecast.current.temperature.value,
            tempUnit = units.temperature.name,
            feelsLikeTemp = forecast.current.temperature.feelsLike,
            windSpeed = forecast.current.wind.speed,
            windSpeedUnit = units.windSpeed.name,
            precipChance = forecast.current.precipitation.probability,
            todayScoreResult = score.today.result.name,
            alertCount = forecast.alerts.size,
            updatedAtMillis = Clock.System.now().toEpochMilliseconds(),
            activityName = activityName,
        )
}
