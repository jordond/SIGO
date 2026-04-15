package now.shouldigooutside.core.widget

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units

/**
 * Observes [ForecastStateHolder] and widget-relevant settings, pushing updates into
 * [UpdateWidgetDataUseCase] whenever either changes.
 */
public class WidgetForecastObserver(
    private val forecastStateHolder: ForecastStateHolder,
    private val settingsRepo: SettingsRepo,
    private val scoreCalculator: ScoreCalculator,
    private val updateWidgetData: UpdateWidgetDataUseCase,
) {
    private val logger = Logger.withTag("WidgetForecastObserver")

    public fun start(scope: CoroutineScope) {
        scope.launch {
            val widgetSettings = settingsRepo.settings
                .map { WidgetSettingsKey(it.widgetActivity, it.includeAirQuality, it.units) }
                .distinctUntilChanged()

            combine(forecastStateHolder.state, widgetSettings) { result, _ -> result }
                .collect { result ->
                    if (result is AsyncResult.Success) {
                        refresh(result.data)
                    }
                }
        }
    }

    private suspend fun refresh(forecast: Forecast) {
        val settings = settingsRepo.settings.value
        val widgetActivity = settings.widgetActivity
        val preferences = settings.activities[widgetActivity] ?: Preferences.default
        val score = scoreCalculator.calculate(
            forecast,
            preferences,
            settings.includeAirQuality,
        )
        updateWidgetData.update(
            forecast = forecast,
            score = score,
            units = settings.units,
            widgetActivity = widgetActivity,
        )
        logger.d { "Widget data refreshed for $widgetActivity" }
    }

    private data class WidgetSettingsKey(
        val widgetActivity: Activity,
        val includeAirQuality: Boolean,
        val units: Units,
    )
}
