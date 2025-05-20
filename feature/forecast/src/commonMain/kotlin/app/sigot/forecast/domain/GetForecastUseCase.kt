package app.sigot.forecast.domain

import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.domain.forecast.convert
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.core.model.units.Units
import app.sigot.core.platform.ticker
import app.sigot.forecast.domain.DefaultGetForecastUseCase.Companion.refreshInterval
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.minutes

// TODO: Add function for getting location before forecast
public interface GetForecastUseCase {
    public suspend fun forecastFor(
        location: Location,
        units: Units,
    ): Result<Forecast>

    public suspend fun forecastFor(
        location: String,
        units: Units,
    ): Result<Forecast>

    public fun forecastFor(location: Location): Flow<Result<Forecast>>

    public fun forecastFor(location: String): Flow<Result<Forecast>>
}

internal class DefaultGetForecastUseCase(
    private val forecastRepo: ForecastRepo,
    private val settingsRepo: SettingsRepo,
) : GetForecastUseCase {
    override suspend fun forecastFor(
        location: Location,
        units: Units,
    ): Result<Forecast> = forecastRepo.forecastFor(location).map { it.convert(units) }

    override suspend fun forecastFor(
        location: String,
        units: Units,
    ): Result<Forecast> = forecastRepo.forecastFor(location).map { it.convert(units) }

    override fun forecastFor(location: Location): Flow<Result<Forecast>> =
        forecastFlow { forecastRepo.forecastFor(location, force = true) }

    override fun forecastFor(location: String): Flow<Result<Forecast>> =
        forecastFlow { forecastRepo.forecastFor(location, force = true) }

    /**
     * Combines the forecast with the settings to convert the units.
     *
     * The forecast is refreshed every [refreshInterval] minutes, and the forecast is converted to the
     * units specified in the settings when or if the units change.
     */
    private fun forecastFlow(forecast: suspend () -> Result<Forecast>): Flow<Result<Forecast>> =
        combine(
            ticker(refreshInterval).map { forecast() }.distinctUntilChanged(),
            settingsRepo.settings.map { it.preferences.units }.distinctUntilChanged(),
        ) { forecasts, units ->
            forecasts.map { it.convert(units) }
        }

    companion object {
        val refreshInterval = 15.minutes
    }
}
