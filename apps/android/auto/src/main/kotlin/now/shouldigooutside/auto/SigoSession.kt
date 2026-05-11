package now.shouldigooutside.auto

import android.content.Intent
import androidx.car.app.CarContext
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
import kotlinx.coroutines.runBlocking
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.auto.location.CarLocationProvider
import now.shouldigooutside.auto.screens.AlertsTemplateBuilder
import now.shouldigooutside.auto.screens.HomeScreen
import now.shouldigooutside.auto.screens.HomeScreenDeps
import now.shouldigooutside.auto.screens.HomeTemplateBuilder
import now.shouldigooutside.auto.screens.HourlyTemplateBuilder
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.score.ActivityForecastScore
import kotlin.time.Clock

internal data class SigoSessionDeps(
    val forecastStateHolder: ForecastStateHolder,
    val settingsRepo: SettingsRepo,
    val getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
    val carLocationProviderFactory: (CarContext) -> CarLocationProvider?,
    val stringsProvider: suspend () -> AutoStrings,
)

internal class SigoSession(
    private val deps: SigoSessionDeps,
) : Session(),
    DefaultLifecycleObserver {
    private val logger = Logger.withTag("SigoSession")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val activityScoresFlow = MutableStateFlow(persistentListOf<ActivityForecastScore>())

    private val orchestrator = SigoSessionOrchestrator(
        forecastStateHolder = deps.forecastStateHolder,
        settingsRepo = deps.settingsRepo,
        getActivitiesScoreUseCase = deps.getActivitiesScoreUseCase,
        activityScoresSink = activityScoresFlow,
    )

    private lateinit var strings: AutoStrings

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreateScreen(intent: Intent): Screen {
        strings = runBlocking { deps.stringsProvider() }
        val formatter = CarForecastFormatter(strings)
        val homeBuilder = HomeTemplateBuilder(formatter, strings, nowProvider = { Clock.System.now() })
        val hourlyBuilder = HourlyTemplateBuilder(formatter, strings, nowProvider = { Clock.System.now() })
        val alertsBuilder = AlertsTemplateBuilder(formatter, strings)

        return HomeScreen(
            carContext = carContext,
            deps = HomeScreenDeps(
                forecastState = deps.forecastStateHolder.state,
                settings = deps.settingsRepo.settings,
                activityScores = activityScoresFlow,
                strings = strings,
                onRefresh = { deps.forecastStateHolder.fetch() },
                nowProvider = { Clock.System.now() },
                hourlyTemplateBuilder = hourlyBuilder,
                alertsTemplateBuilder = alertsBuilder,
            ),
            templateBuilder = homeBuilder,
        )
    }

    override fun onStart(owner: LifecycleOwner) {
        val carLocationProvider = deps.carLocationProviderFactory(carContext)
        orchestrator.start(scope, carLocationProvider) { invalidateTopScreen() }
    }

    override fun onStop(owner: LifecycleOwner) {
        scope.coroutineContext.cancelChildren()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        scope.cancel()
    }

    private fun invalidateTopScreen() {
        runCatching {
            carContext.getCarService(ScreenManager::class.java).top.invalidate()
        }.onFailure { logger.w(it) { "invalidate() failed" } }
    }
}
