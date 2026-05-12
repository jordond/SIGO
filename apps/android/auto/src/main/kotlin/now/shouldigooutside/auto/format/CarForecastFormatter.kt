package now.shouldigooutside.auto.format

import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.Row
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.WeatherReason
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.score.dominantReason
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Instant
import java.util.TimeZone as JavaTimeZone

internal class CarForecastFormatter(
    private val strings: AutoStrings,
    private val iconProvider: AutoIconProvider,
) {
    private val shortTimeFormat: DateFormat = DateFormat
        .getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        .apply { timeZone = JavaTimeZone.getDefault() }

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

        if (forecast != null && current != null) {
            val staleMinutes = (now - forecast.instant).inWholeMinutes.toInt()
            val staleLabel: String? = if (staleMinutes >= STALE_THRESHOLD_MINUTES) {
                if (staleMinutes < 60) {
                    strings.staleMinutes(staleMinutes)
                } else {
                    strings.staleHours(staleMinutes / 60)
                }
            } else {
                null
            }

            val rows = buildList<Row> {
                val tempText = formatTemp(current.temperature.value, state.units)
                val verdictTitle = if (state.currentScore != null) {
                    "${scoreSymbol(state.currentScore)} ${scoreLabel(state.currentScore)} — $tempText"
                } else {
                    tempText
                }
                val conditions = listOfNotNull(
                    strings.feelsLikeShort(formatTemp(current.temperature.feelsLike, state.units)),
                    strings.windShort(formatWind(current.wind.speed, state.units)),
                    if (current.precipitation.probability > 0) {
                        strings.precipShort(current.precipitation.probability)
                    } else {
                        null
                    },
                    reasonTag(state.currentReason.takeIf { state.currentScore != ScoreResult.Yes }),
                ).joinToString(separator = " · ")

                val verdictRow = Row.Builder().setTitle(verdictTitle).addText(conditions)
                if (state.currentScore != null) {
                    verdictRow.setImage(iconProvider.scoreIcon(state.currentScore), Row.IMAGE_TYPE_ICON)
                }
                add(verdictRow.build())

                val name = state.locationName
                if (name != null) {
                    val locationRow = Row
                        .Builder()
                        .setTitle(name)
                        .setImage(iconProvider.locationIcon(), Row.IMAGE_TYPE_ICON)
                    staleLabel?.let { locationRow.addText(it) }
                    add(locationRow.build())
                } else if (staleLabel != null) {
                    add(Row.Builder().setTitle(staleLabel).build())
                }
            }.take(PANE_ROW_CAP)

            rows.forEach { builder.addRow(it) }

            val alertCount = forecast.alerts.size
            if (alertCount > 0) {
                alertsAction = Action
                    .Builder()
                    .setTitle(strings.alertsCount(alertCount))
                    .setOnClickListener { onAlerts() }
                    .build()
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

    private fun scoreSymbol(result: ScoreResult): String =
        when (result) {
            ScoreResult.Yes -> strings.scoreSymbolYes
            ScoreResult.No -> strings.scoreSymbolNo
            ScoreResult.Maybe -> strings.scoreSymbolMaybe
        }

    private fun reasonLabel(reason: WeatherReason): String =
        when (reason) {
            WeatherReason.Wind -> strings.reasonWind
            WeatherReason.Temperature -> strings.reasonTemperature
            WeatherReason.Precipitation -> strings.reasonPrecipitation
            WeatherReason.SevereWeather -> strings.reasonSevereWeather
            WeatherReason.AirQuality -> strings.reasonAirQuality
        }

    private fun reasonTag(reason: WeatherReason?): String? =
        reason?.let { "${strings.reasonPrefix} ${reasonLabel(it)}" }

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
        hourScores: List<Score>,
    ): ItemList {
        val list = ItemList.Builder()
        val futureHours = forecast.today.hours
            .asSequence()
            .filter { it.instant >= now }
            .take(HOURLY_ROW_CAP)
            .toList()

        futureHours.forEachIndexed { index, hour ->
            val precipProb = hour.precipitation.probability
            val timeLabel = formatHour(hour.instant)
            val tempText = formatTemp(hour.temperature.value, units)
            val condition = conditionGlyph(precipProb, hour.cloudCoverPercent)
            val title = "$timeLabel · $tempText · $condition"

            val score = hourScores.getOrNull(index)
            val hourReason = if (score != null && score.result != ScoreResult.Yes) {
                score.reasons.dominantReason()
            } else {
                null
            }
            val detailParts = listOfNotNull(
                strings.windShort(formatWind(hour.wind.speed, units)),
                if (precipProb > 0) strings.precipShort(precipProb) else null,
                reasonTag(hourReason),
            )

            val row = Row.Builder().setTitle(title).addText(detailParts.joinToString(separator = " · "))
            score?.let { row.setImage(iconProvider.scoreIcon(it.result), Row.IMAGE_TYPE_ICON) }
            list.addItem(row.build())
        }
        return list.build()
    }

    private fun formatHour(instant: Instant): String =
        shortTimeFormat.format(Date(instant.toEpochMilliseconds()))

    private fun conditionGlyph(
        precipProbability: Int,
        cloudCoverPercent: Int,
    ): String =
        when {
            precipProbability >= 70 -> strings.conditionRain
            precipProbability >= 30 -> strings.conditionShowers
            cloudCoverPercent >= 70 -> strings.conditionCloudy
            cloudCoverPercent >= 30 -> strings.conditionPartlyCloudy
            else -> strings.conditionSunny
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

    private companion object {
        const val STALE_THRESHOLD_MINUTES = 60
        const val HOURLY_ROW_CAP = 12
        const val PANE_ROW_CAP = 4
    }
}

internal fun buildAlertDetail(alert: Alert): MessageTemplate {
    val message = alert.description
        .takeIf { it.isNotBlank() }
        ?: alert.headline?.takeIf { it.isNotBlank() }
        ?: alert.title
    return MessageTemplate
        .Builder(message)
        .setTitle(alert.title)
        .setHeaderAction(Action.BACK)
        .build()
}

internal data class AutoStrings(
    val appName: String,
    val refresh: String,
    val hourlyForecast: String,
    val openPhone: String,
    val locationFailed: String,
    val forecastUnavailable: String,
    val retry: String,
    val scoreYes: String,
    val scoreNo: String,
    val scoreMaybe: String,
    val scoreSymbolYes: String,
    val scoreSymbolNo: String,
    val scoreSymbolMaybe: String,
    val conditionSunny: String,
    val conditionPartlyCloudy: String,
    val conditionCloudy: String,
    val conditionShowers: String,
    val conditionRain: String,
    val reasonPrefix: String,
    val reasonWind: String,
    val reasonTemperature: String,
    val reasonPrecipitation: String,
    val reasonSevereWeather: String,
    val reasonAirQuality: String,
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
