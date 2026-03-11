package app.sigot.forecast.domain

import app.sigot.core.config.AppConfigRepo
import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.foundation.ktx.ensureExecutionTime
import app.sigot.core.model.AsyncResult
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.LocationResult
import app.sigot.core.model.mapSuccess
import app.sigot.core.model.toAsyncResult
import co.touchlab.kermit.Logger
import dev.stateholder.stateContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class DefaultForecastStateHolder(
    private val locationRepo: LocationRepo,
    private val getForecastUseCase: GetForecastUseCase,
    private val settingsRepo: SettingsRepo,
    private val appConfigRepo: AppConfigRepo,
    private val scoreCalculator: ScoreCalculator,
    private val scope: CoroutineScope,
) : ForecastStateHolder {
    private val logger = Logger.withTag("ForecastStateHolder")
    private val delayDuration = appConfigRepo.value.maxCacheAge

    private val container = stateContainer<AsyncResult<Forecast>?>(null)
    override val state: StateFlow<AsyncResult<ForecastData>> =
        combine(
            container.state.filterNotNull().distinctUntilChanged(),
            settingsRepo.settings.map { it.preferences }.distinctUntilChanged(),
            transform = { forecastResult, preferences -> forecastResult to preferences },
        ).map { (forecastResult, preferences) ->
            forecastResult.mapSuccess { forecast ->
                ForecastData(
                    forecast = forecast,
                    score = scoreCalculator.calculate(forecast, preferences),
                )
            }
        }.stateIn(scope, SharingStarted.WhileSubscribed(5000), AsyncResult.Loading)

    private var fetchJob: Job? = null
    private var refreshJob: Job? = null

    override fun fetch() {
        if (fetchJob?.isActive == true) return
        fetchJob = scope.launch {
            ensureExecutionTime(appConfigRepo.value.minimumExecutionDelay) {
                logger.d { "Fetching forecast..." }
                getForecast()
            }
        }
    }

    override fun start() {
        if (refreshJob?.isActive == true) return

        logger.d { "Starting refresh ticker" }
        refreshJob = scope.launch {
            while (isActive) {
                fetch()
                delay(delayDuration)
                logger.d { "Refreshing forecast..." }
            }
        }
    }

    override fun stop() {
        logger.d { "Stopping refresh ticker" }
        refreshJob?.cancel()
    }

    private suspend fun getForecast() {
        container.update { AsyncResult.Loading }

        val location = locationRepo.location()
        val units = settingsRepo.settings.value.preferences.units
        when (location) {
            is LocationResult.Failed -> {
                container.update { AsyncResult.Error(location) }
            }
            is LocationResult.Success -> {
                val forecastResult = getForecastUseCase.forecastFor(location.location, units)
                container.update { forecastResult.toAsyncResult() }
            }
        }
    }
}
