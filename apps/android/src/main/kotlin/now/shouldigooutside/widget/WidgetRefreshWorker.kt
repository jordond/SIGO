package now.shouldigooutside.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.widget.UpdateWidgetDataUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params),
    KoinComponent {
    private val logger = Logger.withTag("WidgetRefreshWorker")
    private val getForecastUseCase: GetForecastUseCase by inject()
    private val settingsRepo: SettingsRepo by inject()
    private val scoreCalculator: ScoreCalculator by inject()
    private val updateWidgetData: UpdateWidgetDataUseCase by inject()

    override suspend fun doWork(): Result {
        return try {
            logger.d { "Starting widget refresh..." }

            val settings = settingsRepo.settings.value
            val location = settings.location
            if (location == null) {
                logger.w { "No cached location available for widget refresh" }
                return Result.failure()
            }

            val units = settings.units
            val forecastResult = getForecastUseCase.forecastFor(location, units)
            val forecast = forecastResult.getOrNull()
            if (forecast == null) {
                logger.w { "Failed to fetch forecast for widget refresh" }
                return Result.retry()
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

            SigoWidget().updateAll(applicationContext)
            logger.d { "Widget refresh complete" }

            Result.success()
        } catch (e: Exception) {
            logger.e(e) { "Widget refresh failed" }
            Result.retry()
        }
    }
}
