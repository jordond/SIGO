package now.shouldigooutside.core.domain.forecast

import dev.stateholder.StateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast

public interface ForecastStateHolder : StateHolder<AsyncResult<Forecast>?> {
    public override val state: StateFlow<AsyncResult<Forecast>>

    public fun fetch()

    public fun start(scope: CoroutineScope? = null)

    public fun stop()
}
