package now.shouldigooutside.auto.screens

import androidx.car.app.model.Action
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast

internal class AlertsTemplateBuilder(
    private val formatter: CarForecastFormatter,
    private val strings: AutoStrings,
) {
    fun build(
        status: AsyncResult<Forecast>,
        onAlertClick: (Alert) -> Unit,
    ): Template {
        val forecast = (status as? AsyncResult.Success)?.data
            ?: return MessageTemplate
                .Builder(strings.forecastUnavailable)
                .setTitle(strings.alertsCount(0))
                .setHeaderAction(Action.BACK)
                .build()

        val list = formatter.alertsList(forecast.alerts, onAlertClick)

        return ListTemplate
            .Builder()
            .setSingleList(list)
            .setTitle(strings.alertsCount(forecast.alerts.size))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
