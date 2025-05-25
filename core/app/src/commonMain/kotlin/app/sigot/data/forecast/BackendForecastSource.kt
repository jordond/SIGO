package app.sigot.data.forecast

import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.source.ForecastSource

/**
 * Used by the app to fetch forecast data
 */
internal class BackendForecastSource(
    private val settingsRepo: SettingsRepo,
) : ForecastSource {
    private val url: String
        get() = settingsRepo.settings.value.internalSettings.backendApiUrl

    override suspend fun forecastFor(location: Location): Forecast {
        TODO("Need to implement the calls to the backend")
    }

    override suspend fun forecastFor(location: String): Forecast {
        TODO("Need to implement the calls to the backend")
    }
}
