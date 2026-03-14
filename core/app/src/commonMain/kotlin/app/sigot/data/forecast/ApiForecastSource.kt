package app.sigot.data.forecast

import app.sigot.core.api.client.ApiClient
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.source.ForecastSource

/**
 * Used by the app to fetch forecast data
 */
internal class ApiForecastSource(
    private val apiClient: ApiClient,
) : ForecastSource {
    override suspend fun forecastFor(location: Location): Forecast = apiClient.forecast(location).data

    override suspend fun forecastFor(location: String): Forecast =
        throw RuntimeException("Not Supported, API requires a Location object. Call forecastFor(Location)")
}
