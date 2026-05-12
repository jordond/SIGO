package now.shouldigooutside.auto.screens

import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import kotlinx.collections.immutable.PersistentList
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.format.CarAutoHomeState
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.auto.format.HomePaneResult
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.dominantReason
import now.shouldigooutside.core.model.settings.Settings
import kotlin.time.Instant

internal class HomeTemplateBuilder(
    private val formatter: CarForecastFormatter,
    private val strings: AutoStrings,
    private val nowProvider: () -> Instant,
) {
    /** Returns the home pane result, exposed for unit testing without PaneTemplate constraints. */
    internal fun buildResult(
        status: AsyncResult<Forecast>,
        cachedForecast: Forecast?,
        settings: Settings,
        scores: PersistentList<ActivityForecastScore>,
        onRefresh: () -> Unit,
        onHourly: () -> Unit,
        onAlerts: () -> Unit,
    ): HomePaneResult? {
        if (status is AsyncResult.Loading && cachedForecast == null) {
            return null
        }

        val current = scores.forecastScoreFor(settings.selectedActivity)?.current

        val state = CarAutoHomeState(
            status = if (cachedForecast != null) AsyncResult.Success(cachedForecast) else status,
            units = settings.units,
            selectedActivity = settings.selectedActivity,
            currentScore = current?.result,
            currentReason = current?.reasons?.dominantReason(),
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
        cachedForecast: Forecast?,
        settings: Settings,
        scores: PersistentList<ActivityForecastScore>,
        onRefresh: () -> Unit,
        onHourly: () -> Unit,
        onAlerts: () -> Unit,
    ): Template {
        if (status is AsyncResult.Error && cachedForecast == null) {
            return MessageTemplate
                .Builder(strings.forecastUnavailable)
                .setTitle(strings.openPhone)
                .setHeaderAction(Action.APP_ICON)
                .addAction(
                    Action
                        .Builder()
                        .setTitle(strings.retry)
                        .setOnClickListener { onRefresh() }
                        .build(),
                ).build()
        }

        val result = buildResult(
            status = status,
            cachedForecast = cachedForecast,
            settings = settings,
            scores = scores,
            onRefresh = onRefresh,
            onHourly = onHourly,
            onAlerts = onAlerts,
        )

        if (result == null || result.pane.rows.isEmpty()) {
            return PaneTemplate
                .Builder(Pane.Builder().setLoading(true).build())
                .setTitle(strings.openPhone)
                .setHeaderAction(Action.APP_ICON)
                .build()
        }

        val templateBuilder = PaneTemplate
            .Builder(result.pane)
            .setTitle(strings.appName)
            .setHeaderAction(Action.APP_ICON)

        if (result.alertsAction != null) {
            templateBuilder.setActionStrip(
                ActionStrip.Builder().addAction(result.alertsAction).build(),
            )
        }

        return templateBuilder.build()
    }
}
