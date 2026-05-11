package now.shouldigooutside.auto.format

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastBlock
import now.shouldigooutside.test.testForecastDay
import now.shouldigooutside.test.testLocation
import now.shouldigooutside.test.testPrecipitation
import now.shouldigooutside.test.testTemperature
import now.shouldigooutside.test.testWind
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class CarForecastFormatterTest {
    private val now = Instant.fromEpochSeconds(1_715_000_000) // 2024-05-06
    private val strings = fakeAutoStrings()
    private val formatter = CarForecastFormatter(strings)

    @Test
    fun homePane_showsYesVerdictWithTemperature_inMetric() {
        val forecast = testForecast(
            current = testForecastBlock(temperature = testTemperature(value = 22.0)),
        )
        val state = CarAutoHomeState(
            status = AsyncResult.Success(forecast),
            units = Units.Metric,
            selectedActivity = Activity.General,
            currentScore = ScoreResult.Yes,
            locationName = forecast.location.name,
        )

        val pane = formatter.homePane(state, now, onRefresh = {}, onHourly = {}, onAlerts = {})

        val verdictRow = pane.rows.first()
        verdictRow.title.toString() shouldBe "Yes — 22°C"
    }

    @Test
    fun homePane_includesConditionsRow_withFeelsLikeWindPrecip() {
        val forecast = testForecast(
            current = testForecastBlock(
                temperature = testTemperature(value = 22.0, feelsLike = 24.0),
                wind = testWind(speed = 12.0),
                precipitation = testPrecipitation(probability = 10),
            ),
        )
        val state = baseState(forecast)

        val pane = formatter.homePane(state, now, onRefresh = {}, onHourly = {}, onAlerts = {})

        val conditions = pane.rows[1]
        conditions.title.toString() shouldBe "Feels 24°C"
        conditions.texts.map { it.toString() } shouldBe listOf("Wind 12 km/h", "Precip 10%")
    }

    @Test
    fun homePane_includesLocationRow() {
        val forecast = testForecast(location = testLocation(name = "Toronto"))
        val state = baseState(forecast).copy(locationName = "Toronto")

        val pane = formatter.homePane(state, now, onRefresh = {}, onHourly = {}, onAlerts = {})

        pane.rows[2].title.toString() shouldBe "Toronto"
    }

    @Test
    fun homePane_includesBrowsableHourlyRow() {
        val pane = formatter.homePane(
            baseState(testForecast()),
            now,
            onRefresh = {},
            onHourly = {},
            onAlerts = {},
        )

        val hourlyRow = pane.rows[3]
        hourlyRow.title.toString() shouldBe "Hourly forecast"
        hourlyRow.isBrowsable shouldBe true
    }

    @Test
    fun homePane_omitsAlertsRow_whenNoAlerts() {
        val forecast = testForecast(alerts = kotlinx.collections.immutable.persistentListOf())
        val state = baseState(forecast)

        val pane = formatter.homePane(state, now, onRefresh = {}, onHourly = {}, onAlerts = {})

        pane.rows.none { it.title.toString().endsWith("alerts") } shouldBe true
    }

    @Test
    fun homePane_includesAlertsRow_whenAlertsPresent() {
        val forecast = testForecast(
            alerts = kotlinx.collections.immutable.persistentListOf(
                Alert(title = "Storm", description = "desc"),
                Alert(title = "Wind", description = "desc"),
            ),
        )
        val state = baseState(forecast)

        val pane = formatter.homePane(state, now, onRefresh = {}, onHourly = {}, onAlerts = {})

        val alertsRow = pane.rows.last { it.title.toString().endsWith("alerts") }
        alertsRow.title.toString() shouldBe "2 alerts"
        alertsRow.isBrowsable shouldBe true
    }

    @Test
    fun homePane_includesStaleRow_whenForecastOlderThanThreshold() {
        val forecastInstant = now - kotlin.time.Duration.parse("PT3H")
        val forecast = testForecast(instant = forecastInstant)
        val state = baseState(forecast)

        val pane = formatter.homePane(state, now, onRefresh = {}, onHourly = {}, onAlerts = {})

        pane.rows
            .last()
            .title
            .toString() shouldBe "Updated 3 h ago"
    }

    @Test
    fun homePane_addsRefreshAction() {
        val pane = formatter.homePane(
            baseState(testForecast()),
            now,
            onRefresh = {},
            onHourly = {},
            onAlerts = {},
        )

        pane.actions
            .single()
            .title
            .toString() shouldBe "Refresh"
    }

    @Test
    fun hourlyList_returnsRowsForFutureHoursOnly() {
        val baseInstant = now
        val hours = (0..15).map { offset ->
            testForecastBlock(
                instant = baseInstant + offset.hours - 3.hours,
                temperature = testTemperature(value = 20.0 + offset),
            )
        }
        val forecast = testForecast(today = testForecastDay(hours = hours))

        val list = formatter.hourlyList(forecast, Units.Metric, now)

        list.items.size shouldBe 12
    }

    @Test
    fun hourlyList_capsAtTwelveRows() {
        val baseInstant = now
        val hours = (0..30).map { offset ->
            testForecastBlock(instant = baseInstant + offset.hours)
        }
        val forecast = testForecast(today = testForecastDay(hours = hours))

        val list = formatter.hourlyList(forecast, Units.Metric, now)

        list.items.size shouldBe 12
    }

    @Test
    fun alertsList_returnsRowPerAlert() {
        val alerts = listOf(
            Alert(title = "Storm", description = "d", headline = "h1"),
            Alert(title = "Wind", description = "d", headline = "h2"),
        )

        val list = formatter.alertsList(alerts, onAlertClick = {})

        list.items.size shouldBe 2
        (list.items[0] as androidx.car.app.model.Row).title.toString() shouldBe "Storm"
    }

    @Test
    fun alertDetail_returnsMessageTemplateWithDescription() {
        val alert = Alert(
            title = "Storm",
            description = "Heavy rain expected this evening.",
        )

        val template = formatter.alertDetail(alert)

        template.message.toString() shouldBe "Heavy rain expected this evening."
        template.title.toString() shouldBe "Storm"
    }

    private fun baseState(forecast: Forecast) =
        CarAutoHomeState(
            status = AsyncResult.Success(forecast),
            units = Units.Metric,
            selectedActivity = Activity.General,
            currentScore = ScoreResult.Yes,
            locationName = forecast.location.name,
        )
}

internal fun fakeAutoStrings(): AutoStrings =
    AutoStrings(
        refresh = "Refresh",
        hourlyForecast = "Hourly forecast",
        openPhone = "Open phone",
        locationFailed = "Couldn't find your location",
        forecastUnavailable = "Forecast unavailable",
        retry = "Retry",
        scoreYes = "Yes",
        scoreNo = "No",
        scoreMaybe = "Maybe",
        staleMinutes = { "Updated $it min ago" },
        staleHours = { "Updated $it h ago" },
        feelsLikeShort = { "Feels $it" },
        windShort = { "Wind $it" },
        precipShort = { "Precip $it%" },
        alertsCount = { "$it alerts" },
        tempCelsius = "°C",
        tempFahrenheit = "°F",
        tempKelvin = "K",
        windKph = "km/h",
        windMph = "mph",
        windMs = "m/s",
        windKnots = "kn",
    )
