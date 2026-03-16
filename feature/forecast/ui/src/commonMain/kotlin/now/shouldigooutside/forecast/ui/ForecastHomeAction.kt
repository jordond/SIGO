package now.shouldigooutside.forecast.ui

import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.location.Location

internal sealed interface ForecastHomeAction {
    data object Refresh : ForecastHomeAction

    data object ToSettings : ForecastHomeAction

    data object ToPreferences : ForecastHomeAction

    data object ToViewDetails : ForecastHomeAction

    data class ChangePeriod(
        val period: ForecastPeriod,
    ) : ForecastHomeAction

    data object OpenLocationSheet : ForecastHomeAction

    data object CloseLocationSheet : ForecastHomeAction

    data class SearchLocation(
        val query: String,
    ) : ForecastHomeAction

    data class SelectLocation(
        val location: Location,
    ) : ForecastHomeAction

    data object UseCurrentLocation : ForecastHomeAction
}
