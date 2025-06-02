package app.sigot.core.domain.forecast

import app.sigot.core.model.AsyncResult
import app.sigot.core.model.forecast.Forecast
import dev.stateholder.StateHolder
import kotlinx.coroutines.flow.StateFlow

public interface ForecastStateHolder : StateHolder<AsyncResult<Forecast>?> {
    public override val state: StateFlow<AsyncResult<Forecast>?>

    public fun fetch()

    public fun start()

    public fun stop()
}
