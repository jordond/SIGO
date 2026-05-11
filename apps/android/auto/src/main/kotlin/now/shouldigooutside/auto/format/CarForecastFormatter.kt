package now.shouldigooutside.auto.format

import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.Row
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
    ): HomePaneResult {
        val builder = Pane.Builder()
        val forecast = (state.status as? AsyncResult.Success)?.data
        val current = forecast?.current
        var alertsAction: Action? = null

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

            val alertCount = forecast.alerts.size
            if (alertCount > 0) {
                alertsAction = Action
                    .Builder()
                    .setTitle(strings.alertsCount(alertCount))
                    .setOnClickListener { onAlerts() }
                    .build()
            }

            val staleMinutes = (now - forecast.instant).inWholeMinutes.toInt()
            if (staleMinutes >= STALE_THRESHOLD_MINUTES) {
                val label = if (staleMinutes < 60) {
                    strings.staleMinutes(staleMinutes)
                } else {
                    strings.staleHours(staleMinutes / 60)
                }
                builder.addRow(
                    Row
                        .Builder()
                        .setTitle(label)
                        .build(),
                )
            }
        }

        builder.addAction(
            Action
                .Builder()
                .setTitle(strings.refresh)
                .setOnClickListener { onRefresh() }
                .build(),
        )

        builder.addAction(
            Action
                .Builder()
                .setTitle(strings.hourlyForecast)
                .setOnClickListener { onHourly() }
                .build(),
        )

        return HomePaneResult(pane = builder.build(), alertsAction = alertsAction)
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
    ): ItemList {
        val list = ItemList.Builder()
        val futureHours = forecast.today.hours
            .filter { it.instant >= now }
            .take(HOURLY_ROW_CAP)

        futureHours.forEach { hour ->
            val timeLabel = formatHour(hour.instant)
            val title = "$timeLabel · ${formatTemp(hour.temperature.value, units)}"
            val detailParts = buildList {
                add(strings.windShort(formatWind(hour.wind.speed, units)))
                if (hour.precipitation.probability > 0) {
                    add(strings.precipShort(hour.precipitation.probability))
                }
            }
            val detail = detailParts.joinToString(separator = " · ")
            list.addItem(
                Row
                    .Builder()
                    .setTitle(title)
                    .addText(detail)
                    .build(),
            )
        }
        return list.build()
    }

    private fun formatHour(instant: Instant): String {
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hour12 = ((local.hour + 11) % 12) + 1
        val suffix = if (local.hour < 12) "AM" else "PM"
        return "$hour12 $suffix"
    }

    fun alertsList(
        alerts: List<Alert>,
        onAlertClick: (Alert) -> Unit,
    ): ItemList {
        val list = ItemList.Builder()
        alerts.forEach { alert ->
            list.addItem(
                Row
                    .Builder()
                    .setTitle(alert.title)
                    .also { row -> alert.headline?.let { row.addText(it) } }
                    .setBrowsable(true)
                    .setOnClickListener { onAlertClick(alert) }
                    .build(),
            )
        }
        return list.build()
    }

    fun alertDetail(alert: Alert): MessageTemplate =
        MessageTemplate
            .Builder(alert.description)
            .setTitle(alert.title)
            .setHeaderAction(Action.BACK)
            .build()

    private companion object {
        const val STALE_THRESHOLD_MINUTES = 60
        const val HOURLY_ROW_CAP = 12
    }
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
