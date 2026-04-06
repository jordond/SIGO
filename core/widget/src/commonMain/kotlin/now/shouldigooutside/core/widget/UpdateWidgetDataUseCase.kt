package now.shouldigooutside.core.widget

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Activity
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
            activity = widgetActivity,
        )
        widgetDataStore.save(widgetData)
        widgetNotifier.notifyUpdate()
    }
}
