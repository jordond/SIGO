package app.sigot.forecast.ui

import app.sigot.core.model.forecast.ForecastPeriod

internal sealed interface ForecastHomeAction {
    data object Refresh : ForecastHomeAction

    data object ToSettings : ForecastHomeAction

    data object ToPreferences : ForecastHomeAction

    data object ToViewDetails : ForecastHomeAction

    data class ChangePeriod(
        val period: ForecastPeriod,
    ) : ForecastHomeAction
}
