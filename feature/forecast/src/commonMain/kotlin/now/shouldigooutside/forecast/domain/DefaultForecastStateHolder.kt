package now.shouldigooutside.forecast.domain

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
import now.shouldigooutside.core.config.AppConfigRepo
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.ensureExecutionTime
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.ForecastData
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.model.mapSuccess
import now.shouldigooutside.core.model.toAsyncResult

internal class DefaultForecastStateHolder(
    private val locationRepo: LocationRepo,
    private val getForecastUseCase: GetForecastUseCase,
    private val settingsRepo: SettingsRepo,
    private val appConfigRepo: AppConfigRepo,
    private val scoreCalculator: ScoreCalculator,
    private val coroutineScope: CoroutineScope,
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
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), AsyncResult.Loading)

    private var fetchJob: Job? = null
    private var refreshJob: Job? = null

    override fun fetch() {
        if (fetchJob?.isActive == true) return
        fetchJob = coroutineScope.launch {
            ensureExecutionTime(appConfigRepo.value.minimumExecutionDelay) {
                logger.d { "Fetching forecast..." }
                getForecast()
            }
        }
    }

    override fun start(scope: CoroutineScope?) {
        if (refreshJob?.isActive == true) return

        logger.d { "Starting refresh ticker" }
        refreshJob = (scope ?: coroutineScope).launch {
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
        val units = settingsRepo.settings.value.units
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
