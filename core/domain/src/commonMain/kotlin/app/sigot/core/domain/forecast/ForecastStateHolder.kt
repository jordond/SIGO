package app.sigot.core.domain.forecast

import app.sigot.core.model.AsyncResult
import app.sigot.core.model.ForecastData
import dev.stateholder.StateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

public interface ForecastStateHolder : StateHolder<AsyncResult<ForecastData>?> {
    public override val state: StateFlow<AsyncResult<ForecastData>>

    public fun fetch()

    public fun start(scope: CoroutineScope? = null)

    public fun stop()
}
