package now.shouldigooutside.auto.format

import androidx.car.app.CarContext
import androidx.car.app.model.ItemList
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Instant

internal class CarForecastFormatter(
    private val carContext: CarContext,
    private val strings: AutoStrings,
) {
    fun homePane(
        state: CarAutoHomeState,
        now: Instant,
        onRefresh: () -> Unit,
        onHourly: () -> Unit,
        onAlerts: () -> Unit,
    ): Pane = TODO("Implemented in tasks 4-6")

    fun hourlyList(
        forecast: Forecast,
        units: Units,
        now: Instant,
    ): ItemList = TODO("Implemented in task 7")

    fun alertsList(
        alerts: List<Alert>,
        onAlertClick: (Alert) -> Unit,
    ): ItemList = TODO("Implemented in task 8")

    fun alertDetail(alert: Alert): MessageTemplate = TODO("Implemented in task 8")
}

internal data class AutoStrings(
    val refresh: String,
    val hourlyForecast: String,
    val openPhone: String,
    val locationFailed: String,
    val forecastUnavailable: String,
    val retry: String,
    val scoreYes: String,
    val scoreNo: String,
    val scoreMaybe: String,
    val staleMinutes: (Int) -> String,
    val staleHours: (Int) -> String,
    val feelsLikeShort: (String) -> String,
    val windShort: (String) -> String,
    val precipShort: (Int) -> String,
    val alertsCount: (Int) -> String,
    val tempCelsius: String,
    val tempFahrenheit: String,
    val tempKelvin: String,
    val windKph: String,
    val windMph: String,
    val windMs: String,
    val windKnots: String,
)
