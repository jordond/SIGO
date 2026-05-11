package now.shouldigooutside.auto.screens

import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import kotlinx.collections.immutable.PersistentList
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.format.CarAutoHomeState
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.settings.Settings
import kotlin.time.Instant

internal class HomeTemplateBuilder(
    private val formatter: CarForecastFormatter,
    private val strings: AutoStrings,
    private val nowProvider: () -> Instant,
) {
    private var lastSuccess: Forecast? = null

    /** Returns the [Pane] for the home screen, exposed for unit testing without PaneTemplate constraints. */
    internal fun buildPane(
        status: AsyncResult<Forecast>,
        settings: Settings,
        scores: PersistentList<ActivityForecastScore>,
        onRefresh: () -> Unit,
        onHourly: () -> Unit,
        onAlerts: () -> Unit,
    ): Pane? {
        if (status is AsyncResult.Success) lastSuccess = status.data
        val cachedForecast = (status as? AsyncResult.Success)?.data ?: lastSuccess

        if (status is AsyncResult.Loading && cachedForecast == null) {
            return null
        }

        val currentScore: ScoreResult? = scores
            .firstOrNull { it.activity == settings.selectedActivity }
            ?.score
            ?.current
            ?.result

        val state = CarAutoHomeState(
            status = if (cachedForecast != null) AsyncResult.Success(cachedForecast) else status,
            units = settings.units,
            selectedActivity = settings.selectedActivity,
            currentScore = currentScore,
            locationName = cachedForecast?.location?.name ?: settings.location?.name,
        )

        return formatter.homePane(
            state = state,
            now = nowProvider(),
            onRefresh = onRefresh,
            onHourly = onHourly,
            onAlerts = onAlerts,
        )
    }

    fun build(
        status: AsyncResult<Forecast>,
        settings: Settings,
        scores: PersistentList<ActivityForecastScore>,
        onRefresh: () -> Unit,
        onHourly: () -> Unit,
        onAlerts: () -> Unit,
    ): Template {
        val pane = buildPane(
            status = status,
            settings = settings,
            scores = scores,
            onRefresh = onRefresh,
            onHourly = onHourly,
            onAlerts = onAlerts,
        )

        if (pane == null) {
            return PaneTemplate
                .Builder(Pane.Builder().setLoading(true).build())
                .setTitle(strings.openPhone)
                .setHeaderAction(Action.APP_ICON)
                .build()
        }

        return PaneTemplate
            .Builder(pane)
            .setTitle(strings.openPhone)
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}
