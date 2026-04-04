package now.shouldigooutside.widget

import co.touchlab.kermit.Logger
import kotlinx.coroutines.runBlocking
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.widget.UpdateWidgetDataUseCase
import now.shouldigooutside.core.widget.WidgetData
import now.shouldigooutside.core.widget.WidgetDataMapper
import now.shouldigooutside.core.widget.WidgetDataStore
import now.shouldigooutside.core.widget.widgetDisplayName
import now.shouldigooutside.di.initKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext

public object WidgetRefresher : KoinComponent {
    private val logger = Logger.withTag("WidgetRefresher")

    private val settingsRepo: SettingsRepo by inject()
    private val getForecastUseCase: GetForecastUseCase by inject()
    private val scoreCalculator: ScoreCalculator by inject()
    private val widgetDataStore: WidgetDataStore by inject()

    public fun ensureInitialized() {
        if (GlobalContext.getOrNull() == null) {
            initKoin()
        }
    }

    public fun refresh(): WidgetData? =
        runBlocking {
            try {
                ensureInitialized()

                logger.d { "Starting iOS widget refresh..." }

                val settings = settingsRepo.settings.value
                val location = settings.location
                if (location == null) {
                    logger.w { "No cached location for widget refresh" }
                    return@runBlocking widgetDataStore.load()
                }

                val units = settings.units
                val forecast = getForecastUseCase.forecastFor(location, units).getOrNull()
                if (forecast == null) {
                    logger.w { "Failed to fetch forecast for widget refresh" }
                    return@runBlocking widgetDataStore.load()
                }

                val widgetActivity = settings.widgetActivity
                val preferences = settings.activities[widgetActivity] ?: Preferences.default
                val score = scoreCalculator.calculate(forecast, preferences, settings.includeAirQuality)

                val widgetData = WidgetDataMapper.map(
                    forecast = forecast,
                    score = score,
                    units = units,
                    activityName = widgetActivity.widgetDisplayName(),
                )
                widgetDataStore.save(widgetData)

                logger.d { "iOS widget refresh complete" }
                widgetData
            } catch (e: Exception) {
                logger.e(e) { "iOS widget refresh failed" }
                widgetDataStore.load()
            }
        }
}
