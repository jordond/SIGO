package now.shouldigooutside.core.model

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastDay
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.forecast.Precipitation
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.forecast.Temperature
import now.shouldigooutside.core.model.forecast.Wind
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.Units
import kotlin.test.Test
import kotlin.time.Instant

private fun makeBlock(instant: Instant = Instant.fromEpochSeconds(0)): ForecastBlock =
    ForecastBlock(
        instant = instant,
        humidity = 50.0,
        cloudCoverPercent = 20,
        temperature = Temperature(value = 293.0, feelsLike = 293.0, max = 295.0, min = 290.0),
        precipitation = Precipitation(amount = 0.0, probability = 0, types = emptySet()),
        wind = Wind(
            speed = 5.0,
            gust = null,
            directionDegree = 180.0,
            maxSpeed = null,
            meanSpeed = null,
            minSpeed = null,
        ),
        pressure = 1013.0,
        uvIndex = 3,
        visibility = 10.0,
        severeWeatherRisk = SevereWeatherRisk.None,
        airQuality = AirQuality(1),
    )

private fun makeScore(result: ScoreResult = ScoreResult.Yes): Score =
    Score(
        result = result,
        reasons = Reasons(
            wind = ReasonValue.Inside,
            temperature = ReasonValue.Inside,
            precipitation = ReasonValue.Inside,
            severeWeather = ReasonValue.Inside,
            airQuality = ReasonValue.Inside,
        ),
    )

private fun makeLocation(): Location = Location(latitude = 51.5, longitude = -0.1)

private fun makeData(
    currentBlock: ForecastBlock = makeBlock(Instant.fromEpochSeconds(1000)),
    todayBlock: ForecastBlock = makeBlock(Instant.fromEpochSeconds(2000)),
    todayHours: List<ForecastBlock> = emptyList(),
    days: List<ForecastDay> = emptyList(),
    currentScore: Score = makeScore(ScoreResult.Yes),
    todayScore: Score = makeScore(ScoreResult.Maybe),
    hourScores: List<Score> = emptyList(),
    dayScores: List<Score> = emptyList(),
): ForecastData {
    val forecast = Forecast(
        location = makeLocation(),
        current = currentBlock,
        today = ForecastDay(block = todayBlock, hours = todayHours),
        days = days,
        alerts = emptyList(),
        units = Units.Metric,
        instant = Instant.fromEpochSeconds(1000),
    )
    val score = ForecastScore(
        current = currentScore,
        hours = hourScores,
        today = todayScore,
        days = dayScores,
    )
    return ForecastData(forecast = forecast, score = score)
}

class ForecastDataTest {
    @Test
    fun forPeriodNow_returnsCurrentBlockAndScore() {
        val currentBlock = makeBlock(Instant.fromEpochSeconds(1000))
        val currentScore = makeScore(ScoreResult.Yes)
        val data = makeData(currentBlock = currentBlock, currentScore = currentScore)

        val result = data.forPeriod(ForecastPeriod.Now)

        result?.period shouldBe ForecastPeriod.Now
        result?.forecast shouldBe currentBlock
        result?.score shouldBe currentScore
    }

    @Test
    fun forPeriodToday_returnsTodayBlockAndScore() {
        val todayBlock = makeBlock(Instant.fromEpochSeconds(2000))
        val todayScore = makeScore(ScoreResult.Maybe)
        val data = makeData(todayBlock = todayBlock, todayScore = todayScore)

        val result = data.forPeriod(ForecastPeriod.Today)

        result?.period shouldBe ForecastPeriod.Today
        result?.forecast shouldBe todayBlock
        result?.score shouldBe todayScore
    }

    @Test
    fun forPeriodNextHour_whenHoursPresent_returnsFirstHour() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val hourScore0 = makeScore(ScoreResult.Yes)
        val data = makeData(
            todayHours = listOf(hour0),
            hourScores = listOf(hourScore0),
        )

        val result = data.forPeriod(ForecastPeriod.NextHour)

        result?.forecast shouldBe hour0
        result?.score shouldBe hourScore0
    }

    @Test
    fun forPeriodNextHour_whenNoHours_returnsNull() {
        val data = makeData(todayHours = emptyList(), hourScores = emptyList())
        data.forPeriod(ForecastPeriod.NextHour) shouldBe null
    }

    @Test
    fun forPeriodNextHour2_whenTwoHoursPresent_returnsSecondHour() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val hour1 = makeBlock(Instant.fromEpochSeconds(7200))
        val score0 = makeScore(ScoreResult.Yes)
        val score1 = makeScore(ScoreResult.No)
        val data = makeData(
            todayHours = listOf(hour0, hour1),
            hourScores = listOf(score0, score1),
        )

        val result = data.forPeriod(ForecastPeriod.NextHour2)

        result?.forecast shouldBe hour1
        result?.score shouldBe score1
    }

    @Test
    fun forPeriodNextHour2_whenOnlyOneHour_returnsNull() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val data = makeData(
            todayHours = listOf(hour0),
            hourScores = listOf(makeScore()),
        )
        data.forPeriod(ForecastPeriod.NextHour2) shouldBe null
    }

    @Test
    fun forPeriodNextHour3_whenThreeHoursPresent_returnsThirdHour() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val hour1 = makeBlock(Instant.fromEpochSeconds(7200))
        val hour2 = makeBlock(Instant.fromEpochSeconds(10800))
        val score2 = makeScore(ScoreResult.Maybe)
        val data = makeData(
            todayHours = listOf(hour0, hour1, hour2),
            hourScores = listOf(makeScore(), makeScore(), score2),
        )

        val result = data.forPeriod(ForecastPeriod.NextHour3)

        result?.forecast shouldBe hour2
        result?.score shouldBe score2
    }

    @Test
    fun forPeriodTomorrow_whenDayPresent_returnsTomorrowBlockAndScore() {
        val tomorrowBlock = makeBlock(Instant.fromEpochSeconds(86400))
        val tomorrowDay = ForecastDay(block = tomorrowBlock, hours = emptyList())
        val tomorrowScore = makeScore(ScoreResult.No)
        val data = makeData(
            days = listOf(tomorrowDay),
            dayScores = listOf(tomorrowScore),
        )

        val result = data.forPeriod(ForecastPeriod.Tomorrow)

        result?.period shouldBe ForecastPeriod.Tomorrow
        result?.forecast shouldBe tomorrowBlock
        result?.score shouldBe tomorrowScore
    }

    @Test
    fun forPeriodTomorrow_whenNoDays_returnsNull() {
        val data = makeData(days = emptyList(), dayScores = emptyList())
        data.forPeriod(ForecastPeriod.Tomorrow) shouldBe null
    }

    @Test
    fun forBlock_currentBlock_returnsCurrentScore() {
        val currentBlock = makeBlock(Instant.fromEpochSeconds(1000))
        val currentScore = makeScore(ScoreResult.Yes)
        val data = makeData(currentBlock = currentBlock, currentScore = currentScore)

        data.forBlock(currentBlock) shouldBe currentScore
    }

    @Test
    fun forBlock_todayBlock_returnsTodayScore() {
        val todayBlock = makeBlock(Instant.fromEpochSeconds(2000))
        val todayScore = makeScore(ScoreResult.Maybe)
        val data = makeData(todayBlock = todayBlock, todayScore = todayScore)

        data.forBlock(todayBlock) shouldBe todayScore
    }

    @Test
    fun forBlock_hourBlock_returnsMatchingHourScore() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val hour1 = makeBlock(Instant.fromEpochSeconds(7200))
        val score0 = makeScore(ScoreResult.Yes)
        val score1 = makeScore(ScoreResult.No)
        val data = makeData(
            todayHours = listOf(hour0, hour1),
            hourScores = listOf(score0, score1),
        )

        data.forBlock(hour1) shouldBe score1
    }

    @Test
    fun forBlock_unknownBlock_returnsNull() {
        val unknownBlock = makeBlock(Instant.fromEpochSeconds(99999))
        val data = makeData()

        data.forBlock(unknownBlock) shouldBe null
    }
}
