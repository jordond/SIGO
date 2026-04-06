package now.shouldigooutside.core.widget

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Clock

public object WidgetDataMapper {
    public fun map(
        forecast: Forecast,
        score: ForecastScore,
        units: Units,
        activity: Activity,
    ): WidgetData =
        WidgetData(
            scoreResult = score.current.result,
            locationName = forecast.location.name,
            currentTemp = forecast.current.temperature.value,
            tempUnit = units.temperature,
            feelsLikeTemp = forecast.current.temperature.feelsLike,
            windSpeed = forecast.current.wind.speed,
            windSpeedUnit = units.windSpeed,
            precipChance = forecast.current.precipitation.probability,
            todayScoreResult = score.today.result,
            alertCount = forecast.alerts.size,
            updatedAtMillis = Clock.System.now().toEpochMilliseconds(),
            activityName = activity.displayName,
        )
}
