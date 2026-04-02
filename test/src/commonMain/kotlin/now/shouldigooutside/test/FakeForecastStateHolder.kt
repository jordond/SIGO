package now.shouldigooutside.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast

public class FakeForecastStateHolder(
    initial: AsyncResult<Forecast> = AsyncResult.Loading,
) : ForecastStateHolder {
    private val _state = MutableStateFlow(initial)
    override val state: StateFlow<AsyncResult<Forecast>> = _state.asStateFlow()

    public fun emit(value: AsyncResult<Forecast>) {
        _state.value = value
    }

    override fun fetch() {}

    override fun start(scope: CoroutineScope?) {}

    override fun stop() {}
}
