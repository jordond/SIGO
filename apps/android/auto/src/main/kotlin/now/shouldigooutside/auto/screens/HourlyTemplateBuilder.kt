package now.shouldigooutside.auto.screens

import androidx.car.app.model.Action
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.format.CarForecastFormatter
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.settings.Settings
import kotlin.time.Instant

internal class HourlyTemplateBuilder(
    private val formatter: CarForecastFormatter,
    private val strings: AutoStrings,
    private val nowProvider: () -> Instant,
) {
    fun build(
        status: AsyncResult<Forecast>,
        settings: Settings,
    ): Template {
        val forecast = (status as? AsyncResult.Success)?.data
            ?: return MessageTemplate
                .Builder(strings.forecastUnavailable)
                .setTitle(strings.hourlyForecast)
                .setHeaderAction(Action.BACK)
                .build()

        val list = formatter.hourlyList(forecast, settings.units, nowProvider())

        return ListTemplate
            .Builder()
            .setSingleList(list)
            .setTitle(strings.hourlyForecast)
            .setHeaderAction(Action.BACK)
            .build()
    }
}
