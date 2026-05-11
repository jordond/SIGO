package now.shouldigooutside.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.settings.Settings

internal class HourlyScreen(
    carContext: CarContext,
    private val forecastState: StateFlow<AsyncResult<Forecast>>,
    private val settings: StateFlow<Settings>,
    private val templateBuilder: HourlyTemplateBuilder,
) : Screen(carContext) {
    override fun onGetTemplate(): Template =
        templateBuilder.build(
            status = forecastState.value,
            settings = settings.value,
        )
}
