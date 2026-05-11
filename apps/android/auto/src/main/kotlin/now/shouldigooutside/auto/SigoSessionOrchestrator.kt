package now.shouldigooutside.auto

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import now.shouldigooutside.auto.location.CarLocationProvider
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.score.ActivityForecastScore

internal class SigoSessionOrchestrator(
    private val forecastStateHolder: ForecastStateHolder,
    private val settingsRepo: SettingsRepo,
    private val getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
    private val activityScoresSink: MutableStateFlow<PersistentList<ActivityForecastScore>>,
) {
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
            runCatching { carLocationProvider?.primeLocationRepo() }
                .onFailure { /* swallow */ }
            forecastStateHolder.fetch()

            launch {
                forecastStateHolder.state
                    .collect { onInvalidate() }
            }

            launch {
                getActivitiesScoreUseCase.scoresFlow().collect { scores ->
                    activityScoresSink.value = scores.toPersistentList()
                }
            }
        }
    }
}
