package now.shouldigooutside.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.auto.SessionRenderContext
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast

internal class AlertsScreen(
    carContext: CarContext,
    private val renderContext: StateFlow<SessionRenderContext?>,
    private val forecastState: StateFlow<AsyncResult<Forecast>>,
) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val ctx = renderContext.value ?: return loadingTemplate()
        return ctx.alertsBuilder.build(
            status = forecastState.value,
            onAlertClick = { alert ->
                screenManager.push(AlertDetailScreen(carContext, alert))
            },
        )
    }
}
