package now.shouldigooutside.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import kotlinx.coroutines.flow.StateFlow
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast

internal class AlertsScreen(
    carContext: CarContext,
    private val forecastState: StateFlow<AsyncResult<Forecast>>,
    private val strings: AutoStrings,
    private val templateBuilder: AlertsTemplateBuilder,
) : Screen(carContext) {
    override fun onGetTemplate(): Template =
        templateBuilder.build(
            status = forecastState.value,
            onAlertClick = { alert ->
                screenManager.push(AlertDetailScreen(carContext, alert, strings))
            },
        )
}
