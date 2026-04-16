package now.shouldigooutside.forecast.ui.forecast

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.forecast.ui.components.Severity

@Immutable
public sealed interface ForecastHomeAction {
    public data object Refresh : ForecastHomeAction

    public data object ToViewDetails : ForecastHomeAction

    public data class ChangePeriod(
        val period: ForecastPeriod,
    ) : ForecastHomeAction

    public data class ChangeActivity(
        val activity: Activity,
    ) : ForecastHomeAction

    public data object OpenLocationSheet : ForecastHomeAction

    public data class OpenSevereWeatherInfo(
        val severity: Severity,
    ) : ForecastHomeAction

    public data object OpenAlerts : ForecastHomeAction
}
