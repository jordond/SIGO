package now.shouldigooutside.auto.format

import androidx.car.app.model.ItemList
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.Row
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import kotlin.math.roundToInt
import kotlin.time.Instant

internal class CarForecastFormatter(
    private val strings: AutoStrings,
) {
    fun homePane(
        state: CarAutoHomeState,
        now: Instant,
        onRefresh: () -> Unit,
        onHourly: () -> Unit,
        onAlerts: () -> Unit,
    ): Pane {
        val builder = Pane.Builder()
        val forecast = (state.status as? AsyncResult.Success)?.data
        val current = forecast?.current

        if (current != null && state.currentScore != null) {
            val verdictText =
                "${scoreLabel(state.currentScore)} — ${formatTemp(current.temperature.value, state.units)}"
            builder.addRow(
                Row
                    .Builder()
                    .setTitle(verdictText)
                    .build(),
            )

            builder.addRow(
                Row
                    .Builder()
                    .setTitle(strings.feelsLikeShort(formatTemp(current.temperature.feelsLike, state.units)))
                    .addText(strings.windShort(formatWind(current.wind.speed, state.units)))
                    .addText(strings.precipShort(current.precipitation.probability))
                    .build(),
            )

            state.locationName?.let { name ->
                builder.addRow(Row.Builder().setTitle(name).build())
            }

            builder.addRow(
                Row
                    .Builder()
                    .setTitle(strings.hourlyForecast)
                    .setBrowsable(true)
                    .setOnClickListener { onHourly() }
                    .build(),
            )
        }

        return builder.build()
    }

    private fun scoreLabel(result: ScoreResult): String =
        when (result) {
            ScoreResult.Yes -> strings.scoreYes
            ScoreResult.No -> strings.scoreNo
            ScoreResult.Maybe -> strings.scoreMaybe
        }

    private fun formatTemp(
        value: Double,
        units: Units,
    ): String {
        val rounded = value.roundToInt()
        val unit = when (units.temperature) {
            TemperatureUnit.Celsius -> strings.tempCelsius
            TemperatureUnit.Fahrenheit -> strings.tempFahrenheit
            TemperatureUnit.Kelvin -> strings.tempKelvin
        }
        return "$rounded$unit"
    }

    private fun formatWind(
        value: Double,
        units: Units,
    ): String {
        val rounded = value.roundToInt()
        val unit = when (units.windSpeed) {
            WindSpeedUnit.KilometerPerHour -> strings.windKph
            WindSpeedUnit.MilePerHour -> strings.windMph
            WindSpeedUnit.MeterPerSecond -> strings.windMs
            WindSpeedUnit.Knot -> strings.windKnots
        }
        return "$rounded $unit"
    }

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
