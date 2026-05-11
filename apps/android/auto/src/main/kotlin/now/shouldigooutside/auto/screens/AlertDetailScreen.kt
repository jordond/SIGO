package now.shouldigooutside.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.core.model.forecast.Alert

internal class AlertDetailScreen(
    carContext: CarContext,
    private val alert: Alert,
    strings: AutoStrings,
) : Screen(carContext) {
    private val formatter = CarForecastFormatter(strings)

    override fun onGetTemplate(): Template = formatter.alertDetail(alert)
}
