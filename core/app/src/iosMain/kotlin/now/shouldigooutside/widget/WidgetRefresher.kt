package now.shouldigooutside.widget

import co.touchlab.kermit.Logger
import kotlinx.coroutines.withTimeout
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.widget.UpdateWidgetDataUseCase
import now.shouldigooutside.core.widget.WidgetData
import now.shouldigooutside.core.widget.WidgetDataStore
import now.shouldigooutside.di.initKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatformTools

// Called from Swift WidgetKit timeline provider as an async suspend function.
// KMP generates `refresh(completionHandler:)` which Swift can `await`.
public object WidgetRefresher : KoinComponent {
    private const val REFRESH_TIMEOUT_MS: Long = 10_000

    private val logger = Logger.withTag("WidgetRefresher")

    private val settingsRepo: SettingsRepo by inject()
    private val getForecastUseCase: GetForecastUseCase by inject()
    private val scoreCalculator: ScoreCalculator by inject()
    private val updateWidgetData: UpdateWidgetDataUseCase by inject()
    private val widgetDataStore: WidgetDataStore by inject()

    public fun ensureInitialized() {
        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            initKoin()
        }
    }

    public suspend fun refresh(): WidgetData? =
        try {
            withTimeout(REFRESH_TIMEOUT_MS) {
                ensureInitialized()

                logger.d { "Starting iOS widget refresh..." }

                val settings = settingsRepo.settings.value
                val location = settings.location
                if (location == null) {
                    logger.w { "No cached location for widget refresh" }
                    return@withTimeout widgetDataStore.load()
                }

                val units = settings.units
                val forecast = getForecastUseCase.forecastFor(location, units).getOrNull()
                if (forecast == null) {
                    logger.w { "Failed to fetch forecast for widget refresh" }
                    return@withTimeout widgetDataStore.load()
                }

                val widgetActivity = settings.widgetActivity
                val preferences = settings.activities[widgetActivity] ?: Preferences.default
                val score = scoreCalculator.calculate(forecast, preferences, settings.includeAirQuality)
                updateWidgetData.update(
                    forecast = forecast,
                    score = score,
                    units = units,
                    widgetActivity = widgetActivity,
                )

                logger.d { "iOS widget refresh complete" }
                widgetDataStore.load()
            }
        } catch (e: Exception) {
            logger.e(e) { "iOS widget refresh failed" }
            widgetDataStore.load()
        }
}
