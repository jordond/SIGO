package now.shouldigooutside.data.forecast

import now.shouldigooutside.core.api.client.ApiClient
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.forecast.data.source.ForecastSource

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
