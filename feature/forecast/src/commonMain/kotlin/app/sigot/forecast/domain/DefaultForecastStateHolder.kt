package app.sigot.forecast.domain

import app.sigot.core.config.AppConfigRepo
import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.foundation.ktx.ensureExecutionTime
import app.sigot.core.model.AsyncResult
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.LocationResult
import app.sigot.core.model.toAsyncResult
import dev.stateholder.stateContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class DefaultForecastStateHolder(
    private val locationRepo: LocationRepo,
    private val getForecastUseCase: GetForecastUseCase,
    private val settingsRepo: SettingsRepo,
    private val appConfigRepo: AppConfigRepo,
    private val scope: CoroutineScope,
) : ForecastStateHolder {
    private val container = stateContainer<AsyncResult<Forecast>?>(null)
    override val state: StateFlow<AsyncResult<Forecast>?> = container.state

    private var fetchJob: Job? = null

    override fun fetch() {
        if (fetchJob?.isActive == true) return
        fetchJob = scope.launch {
            ensureExecutionTime(appConfigRepo.value.minimumExecutionDelay) {
                getForecast()
            }
        }
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
