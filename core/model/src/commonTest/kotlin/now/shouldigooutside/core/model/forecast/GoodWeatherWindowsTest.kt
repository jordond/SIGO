package now.shouldigooutside.core.model.forecast

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class GoodWeatherWindowsTest {
    private val baseInstant = Instant.fromEpochSeconds(1_000_000)

    @Test
    fun nullScoreReturnsEmptyList() {
        val forecast = forecast(hourCount = 3)
        forecast.goodWeatherWindows(null).shouldBeEmpty()
    }

    @Test
    fun emptyHoursReturnsEmptyList() {
        val forecast = forecast(hourCount = 0)
        val score = forecastScore(emptyList())
        forecast.goodWeatherWindows(score).shouldBeEmpty()
    }

    @Test
    fun emptyScoreHoursReturnsEmptyList() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(emptyList())
        forecast.goodWeatherWindows(score).shouldBeEmpty()
    }

    @Test
    fun allNoScoresReturnsEmptyList() {
        val forecast = forecast(hourCount = 4)
        val score = forecastScore(listOf(no, no, no, no))
        forecast.goodWeatherWindows(score).shouldBeEmpty()
    }

    @Test
    fun allMaybeScoresReturnsEmptyList() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(listOf(maybe, maybe, maybe))
        forecast.goodWeatherWindows(score).shouldBeEmpty()
    }

    @Test
    fun allYesScoresReturnsSingleWindow() {
        val forecast = forecast(hourCount = 4)
        val score = forecastScore(listOf(yes, yes, yes, yes))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(3)
    }

    @Test
    fun singleYesHourReturnsSingleWindow() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(listOf(no, yes, no))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(1)
        windows[0].end shouldBe hour(2)
    }

    @Test
    fun contiguousYesWindowInMiddle() {
        val forecast = forecast(hourCount = 6)
        val score = forecastScore(listOf(no, yes, yes, yes, no, no))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(1)
        windows[0].end shouldBe hour(4)
    }

    @Test
    fun windowAtStart() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(yes, yes, no, no, no))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(2)
    }

    @Test
    fun windowAtEnd() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(no, no, no, yes, yes))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(3)
        windows[0].end shouldBe hour(4)
    }

    @Test
    fun multipleWindows() {
        val forecast = forecast(hourCount = 8)
        val score = forecastScore(listOf(yes, yes, no, no, yes, yes, yes, no))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 2
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(2)
        windows[1].start shouldBe hour(4)
        windows[1].end shouldBe hour(7)
    }

    @Test
    fun multipleWindowsWithTrailingYes() {
        val forecast = forecast(hourCount = 7)
        val score = forecastScore(listOf(yes, no, yes, no, no, yes, yes))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 3
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(1)
        windows[1].start shouldBe hour(2)
        windows[1].end shouldBe hour(3)
        windows[2].start shouldBe hour(5)
        windows[2].end shouldBe hour(6)
    }

    @Test
    fun maybeBreaksWindow() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(yes, yes, maybe, yes, yes))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 2
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(2)
        windows[1].start shouldBe hour(3)
        windows[1].end shouldBe hour(4)
    }

    @Test
    fun fewerScoresThanHoursUsesOnlyPairedEntries() {
        val forecast = forecast(hourCount = 6)
        val score = forecastScore(listOf(yes, yes, no))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(2)
    }

    @Test
    fun fewerHoursThanScoresUsesOnlyPairedEntries() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(listOf(no, yes, yes, yes, yes, yes))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(1)
        windows[0].end shouldBe hour(2)
    }

    @Test
    fun singleHourYes() {
        val forecast = forecast(hourCount = 1)
        val score = forecastScore(listOf(yes))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(0)
    }

    @Test
    fun singleHourNo() {
        val forecast = forecast(hourCount = 1)
        val score = forecastScore(listOf(no))

        forecast.goodWeatherWindows(score).shouldBeEmpty()
    }

    @Test
    fun alternatingYesNo() {
        val forecast = forecast(hourCount = 6)
        val score = forecastScore(listOf(yes, no, yes, no, yes, no))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 3
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(1)
        windows[1].start shouldBe hour(2)
        windows[1].end shouldBe hour(3)
        windows[2].start shouldBe hour(4)
        windows[2].end shouldBe hour(5)
    }

    private fun hour(index: Int): Instant = baseInstant + index.hours

    private val yes = Score(ScoreResult.Yes, EmptyReasons)
    private val no = Score(ScoreResult.No, EmptyReasons)
    private val maybe = Score(ScoreResult.Maybe, EmptyReasons)

    private fun forecastScore(hours: List<Score>) =
        ForecastScore(
            current = no,
            hours = hours,
            today = no,
            days = emptyList(),
        )

    private fun forecast(hourCount: Int): Forecast {
        val hours = List(hourCount) { i -> block(baseInstant + i.hours) }
        val dayBlock = block(baseInstant)
        return Forecast(
            location = TestLocation,
            current = dayBlock,
            today = ForecastDay(block = dayBlock, hours = hours),
            days = emptyList(),
            alerts = emptyList(),
            instant = baseInstant,
        )
    }
}

private val TestLocation = now.shouldigooutside.core.model.location.Location(
    latitude = 42.763,
    longitude = -81.878,
    name = "Test",
)

private val EmptyReasons = Reasons(
    wind = ReasonValue.Inside,
    temperature = ReasonValue.Inside,
    precipitation = ReasonValue.Inside,
    severeWeather = ReasonValue.Inside,
    airQuality = ReasonValue.Inside,
)

private fun block(instant: Instant) =
    ForecastBlock(
        instant = instant,
        humidity = 0.0,
        cloudCoverPercent = 0,
        temperature = Temperature(value = 20.0, feelsLike = 20.0, max = 20.0, min = 20.0),
        precipitation = Precipitation(amount = 0.0, probability = 0, types = emptySet()),
        wind = Wind(
            speed = 0.0,
            gust = 0.0,
            directionDegree = 0.0,
            maxSpeed = 0.0,
            meanSpeed = 0.0,
            minSpeed = 0.0,
        ),
        pressure = 1013.0,
        uvIndex = 0,
        visibility = 10.0,
        severeWeatherRisk = SevereWeatherRisk.None,
        airQuality = AirQuality(1),
    )
