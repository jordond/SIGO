package now.shouldigooutside.core.domain

import androidx.compose.runtime.Immutable
import dev.stateholder.StateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import now.shouldigooutside.core.model.forecast.ForecastPeriod

@Immutable
public data class AppState(
    val period: ForecastPeriod = ForecastPeriod.Now,
)

public interface AppStateHolder : StateHolder<AppState> {
    public fun update(period: ForecastPeriod)
}

internal class DefaultAppStateHolder : AppStateHolder {
    private val _state = MutableStateFlow(AppState())
    override val state: StateFlow<AppState> = _state.asStateFlow()

    override fun update(period: ForecastPeriod) {
        _state.value = _state.value.copy(period = period)
    }
}
