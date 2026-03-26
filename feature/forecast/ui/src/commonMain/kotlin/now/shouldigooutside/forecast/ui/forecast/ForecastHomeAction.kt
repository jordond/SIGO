package now.shouldigooutside.forecast.ui.forecast

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.location.Location

@Immutable
public sealed interface ForecastHomeAction {
    public data object Refresh : ForecastHomeAction

    public data object ToViewDetails : ForecastHomeAction

    public data class ChangePeriod(
        val period: ForecastPeriod,
    ) : ForecastHomeAction

    public data object OpenLocationSheet : ForecastHomeAction

    public data object CloseLocationSheet : ForecastHomeAction

    public data class SearchLocation(
        val query: String,
    ) : ForecastHomeAction

    public data class SelectLocation(
        val location: Location,
    ) : ForecastHomeAction

    public data object UseCurrentLocation : ForecastHomeAction
}
