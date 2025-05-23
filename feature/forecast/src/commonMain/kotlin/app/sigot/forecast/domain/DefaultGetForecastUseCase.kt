package app.sigot.forecast.domain

import app.sigot.core.config.AppConfigRepo
import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.convert
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationResult
import app.sigot.core.model.units.Units
import app.sigot.core.platform.ticker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration

internal class DefaultGetForecastUseCase(
    private val locationRepo: LocationRepo,
    private val forecastRepo: ForecastRepo,
    private val settingsRepo: SettingsRepo,
    private val appConfigRepo: AppConfigRepo,
) : GetForecastUseCase {
    private val timeout: Duration
        get() = appConfigRepo.value.maxCacheAge

    /**
     * @see GetForecastUseCase.forecastFor
     */
    override suspend fun forecastFor(
        location: Location,
        units: Units?,
    ): Result<Forecast> = forecastRepo.forecastFor(location).map { forecast -> forecast.convertOrNot(units) }

    /**
     * @see GetForecastUseCase.forecastFor
     */
    override suspend fun forecastFor(
        location: String,
        units: Units?,
    ): Result<Forecast> = forecastRepo.forecastFor(location).map { forecast -> forecast.convertOrNot(units) }

    /**
     * @see GetForecastUseCase.forecast
     */
    override suspend fun forecastForCurrentLocation(units: Units?): Result<Forecast> {
        val result = locationRepo.location()
        return when (result) {
            is LocationResult.Failed -> Result.failure(result)
            is LocationResult.Success -> forecastFor(result.location, units)
        }
    }

    /**
     * @see GetForecastUseCase.forecast
     */
    override fun forecast(): Flow<Result<Forecast>> =
        flow {
            val forecastFlow = ticker(timeout)
                .map { locationRepo.location() }
                .onEach { result ->
                    if (result is LocationResult.Failed) {
                        emit(Result.failure(result))
                    }
                }.filterIsInstance<LocationResult.Success>()
                .map { result ->
                    forecastFor(
                        location = result.location,
                        units = settingsRepo.settings.value.preferences.units,
                    )
                }

            val unitsFlow = settingsRepo.settings.map { it.preferences.units }.distinctUntilChanged()

            combine(
                flow = forecastFlow,
                flow2 = unitsFlow,
                transform = { result, units ->
                    result.map { forecast -> forecast.convert(units) }
                },
            ).collect { emit(it) }
        }.flowOn(Dispatchers.Default).distinctUntilChanged()
}

private fun Forecast.convertOrNot(units: Units?): Forecast {
    if (units == null) return this
    return convert(units)
}
