package app.sigot.forecast.ui

import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.location.Location

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
