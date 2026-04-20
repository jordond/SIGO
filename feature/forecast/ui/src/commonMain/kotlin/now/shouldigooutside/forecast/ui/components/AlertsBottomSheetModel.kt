package now.shouldigooutside.forecast.ui.components

import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Alert

@Stable
internal class AlertsBottomSheetModel(
    forecastStateHolder: ForecastStateHolder,
) : StateViewModel<AlertsBottomSheetModel.State>(
        state(forecastStateHolder),
    ) {
    data class State(
        val alerts: List<Alert> = emptyList(),
    )
}

private fun state(forecastStateHolder: ForecastStateHolder) =
    composedStateProvider(
        initialState = AlertsBottomSheetModel.State(
            alerts = (forecastStateHolder.state.value as? AsyncResult.Success)?.data?.alerts.orEmpty(),
        ),
    ) {
        forecastStateHolder.state.into { status ->
            when (status) {
                is AsyncResult.Success -> copy(alerts = status.data.alerts)
                else -> this
            }
        }
    }
