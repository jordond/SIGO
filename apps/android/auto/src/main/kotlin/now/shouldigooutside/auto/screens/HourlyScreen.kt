package now.shouldigooutside.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.auto.SessionRenderContext
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.settings.Settings

internal class HourlyScreen(
    carContext: CarContext,
    private val renderContext: StateFlow<SessionRenderContext?>,
    private val forecastState: StateFlow<AsyncResult<Forecast>>,
    private val settings: StateFlow<Settings>,
) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val ctx = renderContext.value ?: return loadingTemplate()
        return ctx.hourlyBuilder.build(
            status = forecastState.value,
            settings = settings.value,
        )
    }
}
