package now.shouldigooutside.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import now.shouldigooutside.auto.format.buildAlertDetail
import now.shouldigooutside.core.model.forecast.Alert

internal class AlertDetailScreen(
    carContext: CarContext,
    private val alert: Alert,
) : Screen(carContext) {
    override fun onGetTemplate(): Template = buildAlertDetail(alert)
}
