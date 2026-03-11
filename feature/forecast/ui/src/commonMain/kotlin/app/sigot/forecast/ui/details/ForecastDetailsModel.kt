package app.sigot.forecast.ui.details

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.getOrNull
import app.sigot.forecast.ui.navigation.ForecastDetailsRoute
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Stable
internal class ForecastDetailsModel(
    savedStateHandle: SavedStateHandle,
    forecastStateHolder: ForecastStateHolder,
) : StateViewModel<ForecastDetailsModel.State>(
        composedStateProvider(
            State(
                initialPeriod = savedStateHandle.toRoute<ForecastDetailsRoute>().period,
                data = forecastStateHolder.state.value.getOrNull(),
            ),
        ) {
            forecastStateHolder.state.into { result ->
                val data = result.getOrNull()
                val selected = if (hasLoaded || data == null) {
                    null
                } else {
                    when (initialPeriod) {
                        ForecastPeriod.Today -> data.forecast.today.block
                        ForecastPeriod.Now -> data.forecast.current
                        ForecastPeriod.NextHour ->
                            data.forecast.today.hours
                                .getOrNull(0)
                        ForecastPeriod.NextHour2 ->
                            data.forecast.today.hours
                                .getOrNull(1)
                        ForecastPeriod.NextHour3 ->
                            data.forecast.today.hours
                                .getOrNull(2)
                        ForecastPeriod.Tomorrow -> data.forecast.tomorrow?.block
                    }
                }

                copy(data = result.getOrNull(), selected = selected, hasLoaded = data != null)
            }
        },
    ) {
    init {
        viewModelScope.launch {
            state.first { it.data != null }
        }
    }

    fun select(block: ForecastBlock?) {
        updateState { state ->
            val selected = if (block == null || block == state.selected) null else block
            state.copy(selected = selected)
        }
    }

    data class State(
        val initialPeriod: ForecastPeriod,
        val data: ForecastData?,
        val selected: ForecastBlock? = null,
        val hasLoaded: Boolean = false,
    )
}
