package now.shouldigooutside.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import now.shouldigooutside.auto.di.AutoStringsProvider
import now.shouldigooutside.auto.di.CarLocationProviderFactory
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.auto.format.DefaultAutoIconProvider
import now.shouldigooutside.auto.screens.AlertsTemplateBuilder
import now.shouldigooutside.auto.screens.HomeScreen
import now.shouldigooutside.auto.screens.HomeScreenDeps
import now.shouldigooutside.auto.screens.HomeTemplateBuilder
import now.shouldigooutside.auto.screens.HourlyTemplateBuilder
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ActivityForecastScore
import kotlin.time.Clock

internal data class SigoSessionDeps(
    val forecastStateHolder: ForecastStateHolder,
    val settingsRepo: SettingsRepo,
    val getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
    val carLocationProviderFactory: CarLocationProviderFactory,
    val stringsProvider: AutoStringsProvider,
)

internal class SigoSession(
    private val deps: SigoSessionDeps,
) : Session(),
    DefaultLifecycleObserver {
    private val logger = Logger.withTag("SigoSession")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val activityScoresFlow = MutableStateFlow(persistentListOf<ActivityForecastScore>())
    private val cachedForecastFlow = MutableStateFlow<Forecast?>(null)
    private val renderContextFlow = MutableStateFlow<SessionRenderContext?>(null)
    private val renderContext = renderContextFlow.asStateFlow()
    private val nowProvider: () -> kotlin.time.Instant = { Clock.System.now() }

    private val orchestrator = SigoSessionOrchestrator(
        forecastStateHolder = deps.forecastStateHolder,
        settingsRepo = deps.settingsRepo,
        getActivitiesScoreUseCase = deps.getActivitiesScoreUseCase,
        activityScoresSink = activityScoresFlow,
        cachedForecastSink = cachedForecastFlow,
    )

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreateScreen(intent: Intent): Screen =
        HomeScreen(
            carContext = carContext,
            renderContext = renderContext,
            deps = HomeScreenDeps(
                forecastState = deps.forecastStateHolder.state,
                cachedForecast = cachedForecastFlow,
                settings = deps.settingsRepo.settings,
                activityScores = activityScoresFlow,
                onRefresh = { deps.forecastStateHolder.fetch() },
                nowProvider = nowProvider,
            ),
        )

    override fun onStart(owner: LifecycleOwner) {
        val carLocationProvider = deps.carLocationProviderFactory.invoke(carContext)
        orchestrator.start(scope, carLocationProvider) { invalidateTopScreen() }
        if (renderContextFlow.value == null) {
            scope.launch {
                val ctx = withContext(Dispatchers.Default) { buildRenderContext() }
                renderContextFlow.value = ctx
                invalidateTopScreen()
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        scope.coroutineContext.cancelChildren()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        scope.cancel()
    }

    private suspend fun buildRenderContext(): SessionRenderContext {
        val strings = deps.stringsProvider.invoke()
        val iconProvider = DefaultAutoIconProvider(carContext)
        val formatter = CarForecastFormatter(strings, iconProvider)
        return SessionRenderContext(
            homeBuilder = HomeTemplateBuilder(formatter, strings, nowProvider),
            hourlyBuilder = HourlyTemplateBuilder(formatter, strings, nowProvider),
            alertsBuilder = AlertsTemplateBuilder(formatter, strings),
        )
    }

    private fun invalidateTopScreen() {
        runCatching {
            carContext.getCarService(ScreenManager::class.java).top.invalidate()
        }.onFailure { logger.w(it) { "invalidate() failed" } }
    }
}
