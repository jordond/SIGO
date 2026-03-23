package now.shouldigooutside.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.ForecastData
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.widget.AndroidWidgetDataStore
import now.shouldigooutside.core.widget.WidgetDataMapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params),
    KoinComponent {
    private val logger = Logger.withTag("WidgetRefreshWorker")
    private val locationRepo: LocationRepo by inject()
    private val getForecastUseCase: GetForecastUseCase by inject()
    private val settingsRepo: SettingsRepo by inject()
    private val scoreCalculator: ScoreCalculator by inject()

    override suspend fun doWork(): Result {
        return try {
            logger.d { "Starting widget refresh..." }

            val location = locationRepo.location()
            if (location is LocationResult.Failed) {
                logger.w { "Failed to get location for widget refresh" }
                return Result.retry()
            }

            val successLocation = (location as LocationResult.Success).location
            val preferences = settingsRepo.settings.value.preferences
            val units = preferences.units

            val forecastResult = getForecastUseCase.forecastFor(successLocation, units)
            val forecast = forecastResult.getOrNull()
            if (forecast == null) {
                logger.w { "Failed to fetch forecast for widget refresh" }
                return Result.retry()
            }

            val score = scoreCalculator.calculate(forecast, preferences)
            val forecastData = ForecastData(forecast = forecast, score = score)
            val widgetData = WidgetDataMapper.map(forecastData, units)

            val dataStore = AndroidWidgetDataStore(applicationContext)
            dataStore.save(widgetData)

            SigoWidget().updateAll(applicationContext)
            logger.d { "Widget refresh complete" }

            Result.success()
        } catch (e: Exception) {
            logger.e(e) { "Widget refresh failed" }
            Result.retry()
        }
    }
}
