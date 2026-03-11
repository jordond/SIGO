package app.sigot.forecast.ui.details

import androidx.compose.runtime.Stable
import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.getOrNull
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider

@Stable
internal class ForecastDetailsModel(
    forecastStateHolder: ForecastStateHolder,
) : StateViewModel<ForecastDetailsModel.State>(
        composedStateProvider(State(data = null)) {
            forecastStateHolder.state.into { copy(data = it.getOrNull()) }
        },
    ) {
    fun selectHour(block: ForecastBlock?) {
        updateState { state ->
            val selected = if (block == null || block == state.selected) null else block
            state.copy(selected = selected)
        }
    }

    data class State(
        val data: ForecastData?,
        val selected: ForecastBlock? = null,
    )
}
