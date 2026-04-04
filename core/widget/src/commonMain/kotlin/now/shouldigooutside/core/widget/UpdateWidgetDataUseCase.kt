package now.shouldigooutside.core.widget

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.units.Units

public class UpdateWidgetDataUseCase(
    private val widgetDataStore: WidgetDataStore,
    private val widgetNotifier: WidgetNotifier,
) {
    public fun update(
        forecast: Forecast,
        score: ForecastScore,
        units: Units,
        widgetActivity: Activity,
    ) {
        val widgetData = WidgetDataMapper.map(
            forecast = forecast,
            score = score,
            units = units,
            activityName = widgetActivity.widgetDisplayName(),
        )
        widgetDataStore.save(widgetData)
        widgetNotifier.notifyUpdate()
    }

    public fun updateConfig(
        backendUrl: String,
        clientId: String,
        location: Location,
        preferences: Preferences,
        includeAirQuality: Boolean,
        activityName: String,
    ) {
        val config = WidgetConfig(
            backendUrl = backendUrl,
            clientId = clientId,
            lat = location.latitude,
            lon = location.longitude,
            locationName = location.name,
            minTemp = preferences.minTemperature,
            maxTemp = preferences.maxTemperature,
            maxWind = preferences.windSpeed,
            allowRain = preferences.rain,
            allowSnow = preferences.snow,
            maxAqi = preferences.maxAqi.value,
            includeAirQuality = includeAirQuality,
            activityName = activityName,
        )
        widgetDataStore.saveConfig(config)
    }
}
