package now.shouldigooutside.core.domain.forecast

import dev.stateholder.StateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.ForecastData

public interface ForecastStateHolder : StateHolder<AsyncResult<ForecastData>?> {
    public override val state: StateFlow<AsyncResult<ForecastData>>

    public fun fetch()

    public fun start(scope: CoroutineScope? = null)

    public fun stop()
}
