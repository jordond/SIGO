package now.shouldigooutside.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.auto.SessionRenderContext
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.settings.Settings
import kotlin.time.Instant

internal data class HomeScreenDeps(
    val forecastState: StateFlow<AsyncResult<Forecast>>,
    val settings: StateFlow<Settings>,
    val activityScores: StateFlow<PersistentList<ActivityForecastScore>>,
    val onRefresh: () -> Unit,
    val nowProvider: () -> Instant,
)

internal class HomeScreen(
    carContext: CarContext,
    private val renderContext: StateFlow<SessionRenderContext?>,
    private val deps: HomeScreenDeps,
) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val ctx = renderContext.value ?: return loadingTemplate()
        return ctx.homeBuilder.build(
            status = deps.forecastState.value,
            settings = deps.settings.value,
            scores = deps.activityScores.value,
            onRefresh = deps.onRefresh,
            onHourly = {
                screenManager.push(
                    HourlyScreen(
                        carContext = carContext,
                        renderContext = renderContext,
                        forecastState = deps.forecastState,
                        settings = deps.settings,
                        activityScores = deps.activityScores,
                    ),
                )
            },
            onAlerts = {
                screenManager.push(
                    AlertsScreen(
                        carContext = carContext,
                        renderContext = renderContext,
                        forecastState = deps.forecastState,
                    ),
                )
            },
        )
    }
}
