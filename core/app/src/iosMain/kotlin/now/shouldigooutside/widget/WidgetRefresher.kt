package now.shouldigooutside.widget

import co.touchlab.kermit.Logger
import kotlinx.coroutines.withTimeout
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.widget.UpdateWidgetDataUseCase
import now.shouldigooutside.core.widget.WidgetData
import now.shouldigooutside.core.widget.WidgetDataStore
import now.shouldigooutside.core.widget.WidgetInputStore
import now.shouldigooutside.core.widget.toActivity
import now.shouldigooutside.core.widget.toModel
import now.shouldigooutside.di.initKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatformTools

// Called from Swift WidgetKit timeline provider as an async suspend function.
// KMP generates `refresh(completionHandler:)` which Swift can `await`.
public object WidgetRefresher : KoinComponent {
    private const val REFRESH_TIMEOUT_MS: Long = 10_000

    private val logger = Logger.withTag("WidgetRefresher")

    private val widgetInputStore: WidgetInputStore by inject()
    private val getForecastUseCase: GetForecastUseCase by inject()
    private val scoreCalculator: ScoreCalculator by inject()
    private val updateWidgetData: UpdateWidgetDataUseCase by inject()
    private val widgetDataStore: WidgetDataStore by inject()

    public fun ensureInitialized() {
        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            initKoin()
        }
    }

    public fun loadCached(): WidgetData? {
        ensureInitialized()
        return widgetDataStore.load()
    }

    public suspend fun refresh(): WidgetData? =
        try {
            withTimeout(REFRESH_TIMEOUT_MS) {
                ensureInitialized()

                logger.d { "Starting iOS widget refresh..." }

                val inputs = widgetInputStore.load()
                if (inputs == null) {
                    logger.w { "No widget inputs snapshot available yet" }
                    return@withTimeout widgetDataStore.load()
                }

                val location = inputs.location?.toModel()
                if (location == null) {
                    logger.w { "No cached location for widget refresh" }
                    return@withTimeout widgetDataStore.load()
                }

                val units = inputs.units.toModel()
                val forecast = getForecastUseCase.forecastFor(location, units).getOrNull()
                if (forecast == null) {
                    logger.w { "Failed to fetch forecast for widget refresh" }
                    return@withTimeout widgetDataStore.load()
                }

                val widgetActivity = inputs.toActivity()
                val preferences = inputs.preferences.toModel()
                val score = scoreCalculator.calculate(forecast, preferences, inputs.includeAirQuality)
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
