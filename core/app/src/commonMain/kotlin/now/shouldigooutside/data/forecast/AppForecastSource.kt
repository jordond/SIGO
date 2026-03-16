package now.shouldigooutside.data.forecast

import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.forecast.data.source.ForecastSource

internal class AppForecastSource(
    private val settingsRepo: SettingsRepo,
    private val backendSource: ForecastSource,
    private val directSource: ForecastSource,
) : ForecastSource {
    private val useDirectApi: Boolean
        get() = settingsRepo.settings.value.internalSettings.useDirectApi

    override suspend fun forecastFor(location: Location): Forecast =
        if (useDirectApi) directSource.forecastFor(location) else backendSource.forecastFor(location)

    override suspend fun forecastFor(location: String): Forecast =
        if (useDirectApi) directSource.forecastFor(location) else backendSource.forecastFor(location)
}
