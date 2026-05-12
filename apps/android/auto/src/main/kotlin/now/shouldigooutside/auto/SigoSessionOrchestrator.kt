package now.shouldigooutside.auto

import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import now.shouldigooutside.auto.location.CarLocationProvider
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ActivityForecastScore

internal class SigoSessionOrchestrator(
    private val forecastStateHolder: ForecastStateHolder,
    private val settingsRepo: SettingsRepo,
    private val getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
    private val activityScoresSink: MutableStateFlow<PersistentList<ActivityForecastScore>>,
    private val cachedForecastSink: MutableStateFlow<Forecast?>,
) {
    private val logger = Logger.withTag("SigoSessionOrchestrator")

    /**
     * Starts collectors inside [scope]. Calls [onInvalidate] every time the forecast state
     * emits a new value. StateFlow already suppresses duplicate emissions.
     */
    fun start(
        scope: CoroutineScope,
        carLocationProvider: CarLocationProvider?,
        onInvalidate: () -> Unit,
    ) {
        scope.launch {
            forecastStateHolder.state.collect { result ->
                if (result is AsyncResult.Success) {
                    cachedForecastSink.value = result.data
                }
                onInvalidate()
            }
        }

        scope.launch {
            getActivitiesScoreUseCase.scoresFlow().collect { scores ->
                val next = scores.toPersistentList()
                if (next != activityScoresSink.value) {
                    activityScoresSink.value = next
                    onInvalidate()
                }
            }
        }

        scope.launch {
            runCatching { carLocationProvider?.primeLocationRepo() }
                .onFailure { logger.w(it) { "primeLocationRepo failed" } }
            forecastStateHolder.fetch()
        }
    }
}
