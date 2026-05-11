package now.shouldigooutside.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.settings.Settings
import kotlin.time.Instant

internal data class HomeScreenDeps(
    val forecastState: StateFlow<AsyncResult<Forecast>>,
    val settings: StateFlow<Settings>,
    val activityScores: StateFlow<PersistentList<ActivityForecastScore>>,
    val strings: AutoStrings,
    val onRefresh: () -> Unit,
    val nowProvider: () -> Instant,
    val hourlyTemplateBuilder: HourlyTemplateBuilder,
    val alertsTemplateBuilder: AlertsTemplateBuilder,
)

internal class HomeScreen(
    carContext: CarContext,
    private val deps: HomeScreenDeps,
    private val templateBuilder: HomeTemplateBuilder,
) : Screen(carContext) {
    override fun onGetTemplate(): Template =
        templateBuilder.build(
            status = deps.forecastState.value,
            settings = deps.settings.value,
            scores = deps.activityScores.value,
            onRefresh = deps.onRefresh,
            onHourly = {
                screenManager.push(
                    HourlyScreen(
                        carContext = carContext,
                        forecastState = deps.forecastState,
                        settings = deps.settings,
                        templateBuilder = deps.hourlyTemplateBuilder,
                    ),
                )
            },
            onAlerts = {
                screenManager.push(
                    AlertsScreen(
                        carContext = carContext,
                        forecastState = deps.forecastState,
                        strings = deps.strings,
                        templateBuilder = deps.alertsTemplateBuilder,
                    ),
                )
            },
        )
}
