package now.shouldigooutside.forecast.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import now.shouldigooutside.core.domain.forecast.ForecastRepo
import now.shouldigooutside.core.domain.settings.IsSimulateFailureUseCase
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.forecast.data.source.ForecastSource
import kotlin.coroutines.cancellation.CancellationException

internal class DefaultForecastRepo(
    private val source: ForecastSource,
    private val isSimulateFailure: IsSimulateFailureUseCase? = null,
) : ForecastRepo {
    private val logger = Logger.withTag("DefaultForecastRepo")

    override suspend fun forecastFor(
        location: Location,
        force: Boolean,
    ): Result<Forecast> {
        if (isSimulateFailure?.invoke() == true) {
            return Result.failure(RuntimeException("Simulated failure"))
        }

        logger.d { "Fetching fresh forecast for location=$location" }
        return runCatching {
            withContext(Dispatchers.Default) {
                source.forecastFor(location).copy(location = location)
            }
        }.onFailure { cause ->
            if (cause is CancellationException) throw cause
        }
    }

    override suspend fun forecastFor(
        location: String,
        force: Boolean,
    ): Result<Forecast> {
        if (isSimulateFailure?.invoke() == true) {
            return Result.failure(RuntimeException("Simulated failure"))
        }

        logger.d { "Fetching fresh forecast for location=$location" }
        return runCatching {
            withContext(Dispatchers.Default) {
                source.forecastFor(location)
            }
        }.onFailure { cause ->
            if (cause is CancellationException) throw cause
        }
    }
}
