package now.shouldigooutside.forecast.ui.details

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.model.ForecastData
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.getOrNull
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.forecast.ui.navigation.ForecastDetailsRoute

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
                val newSelected =
                    if (!hasLoaded && data != null) {
                        when (initialPeriod) {
                            ForecastPeriod.Today -> data.forecast.today.block
                            ForecastPeriod.Now -> data.forecast.current
                            ForecastPeriod.NextHour -> data.forecast.hour(0)
                            ForecastPeriod.NextHour2 -> data.forecast.hour(1)
                            ForecastPeriod.NextHour3 -> data.forecast.hour(2)
                            ForecastPeriod.Tomorrow -> data.forecast.tomorrow?.block
                        }
                    } else {
                        selected
                    }
                copy(data = data, selected = newSelected, hasLoaded = data != null)
            }
        },
    ) {
    fun select(block: ForecastBlock?) {
        updateState { state ->
            val selected = if (block == state.selected) null else block
            state.copy(selected = selected)
        }
    }

    data class State(
        val initialPeriod: ForecastPeriod,
        val data: ForecastData?,
        val selected: ForecastBlock? = null,
        val hasLoaded: Boolean = false,
    ) {
        val selectedScore: Score? = selected?.let { data?.forBlock(it) }
    }
}
