package now.shouldigooutside.core.widget

import now.shouldigooutside.core.model.ForecastData
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Clock

public object WidgetDataMapper {
    public fun map(
        data: ForecastData,
        units: Units,
    ): WidgetData =
        WidgetData(
            scoreResult = data.score.current.result.name,
            locationName = data.forecast.location.name,
            currentTemp = data.forecast.current.temperature.value,
            tempUnit = units.temperature.name,
            feelsLikeTemp = data.forecast.current.temperature.feelsLike,
            windSpeed = data.forecast.current.wind.speed,
            windSpeedUnit = units.windSpeed.name,
            precipChance = data.forecast.current.precipitation.probability,
            todayScoreResult = data.score.today.result.name,
            alertCount = data.forecast.alerts.size,
            updatedAtMillis = Clock.System.now().toEpochMilliseconds(),
        )
}
