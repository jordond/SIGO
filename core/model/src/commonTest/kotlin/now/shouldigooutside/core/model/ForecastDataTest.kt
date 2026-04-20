package now.shouldigooutside.core.model

import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastDay
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.forecast.Precipitation
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.forecast.Temperature
import now.shouldigooutside.core.model.forecast.Wind
import now.shouldigooutside.core.model.forecast.blockForPeriod
import now.shouldigooutside.core.model.forecast.scoreForBlock
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

private fun makeForecast(
    currentBlock: ForecastBlock = makeBlock(Instant.fromEpochSeconds(1000)),
    todayBlock: ForecastBlock = makeBlock(Instant.fromEpochSeconds(2000)),
    todayHours: List<ForecastBlock> = emptyList(),
    days: List<ForecastDay> = emptyList(),
): Forecast =
    Forecast(
        location = makeLocation(),
        current = currentBlock,
        today = ForecastDay(block = todayBlock, hours = todayHours),
        days = days,
        alerts = persistentListOf(),
        units = Units.Metric,
        instant = Instant.fromEpochSeconds(1000),
    )

private fun makeScoreData(
    currentScore: Score = makeScore(ScoreResult.Yes),
    todayScore: Score = makeScore(ScoreResult.Maybe),
    hourScores: List<Score> = emptyList(),
    dayScores: List<Score> = emptyList(),
): ForecastScore =
    ForecastScore(
        current = currentScore,
        hours = hourScores,
        today = todayScore,
        days = dayScores,
    )

class ForecastBlockForPeriodTest {
    @Test
    fun nowReturnsCurrentBlock() {
        val currentBlock = makeBlock(Instant.fromEpochSeconds(1000))
        val forecast = makeForecast(currentBlock = currentBlock)

        forecast.blockForPeriod(ForecastPeriod.Now) shouldBe currentBlock
    }

    @Test
    fun todayReturnsTodayBlock() {
        val todayBlock = makeBlock(Instant.fromEpochSeconds(2000))
        val forecast = makeForecast(todayBlock = todayBlock)

        forecast.blockForPeriod(ForecastPeriod.Today) shouldBe todayBlock
    }

    @Test
    fun nextHourReturnsFirstHour() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val forecast = makeForecast(todayHours = listOf(hour0))

        forecast.blockForPeriod(ForecastPeriod.NextHour) shouldBe hour0
    }

    @Test
    fun nextHourReturnsNullWhenNoHours() {
        val forecast = makeForecast(todayHours = emptyList())

        forecast.blockForPeriod(ForecastPeriod.NextHour) shouldBe null
    }

    @Test
    fun nextHour2ReturnsSecondHour() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val hour1 = makeBlock(Instant.fromEpochSeconds(7200))
        val forecast = makeForecast(todayHours = listOf(hour0, hour1))

        forecast.blockForPeriod(ForecastPeriod.NextHour2) shouldBe hour1
    }

    @Test
    fun nextHour2ReturnsNullWhenOnlyOneHour() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val forecast = makeForecast(todayHours = listOf(hour0))

        forecast.blockForPeriod(ForecastPeriod.NextHour2) shouldBe null
    }

    @Test
    fun nextHour3ReturnsThirdHour() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val hour1 = makeBlock(Instant.fromEpochSeconds(7200))
        val hour2 = makeBlock(Instant.fromEpochSeconds(10800))
        val forecast = makeForecast(todayHours = listOf(hour0, hour1, hour2))

        forecast.blockForPeriod(ForecastPeriod.NextHour3) shouldBe hour2
    }

    @Test
    fun tomorrowReturnsTomorrowBlock() {
        val tomorrowBlock = makeBlock(Instant.fromEpochSeconds(86400))
        val tomorrowDay = ForecastDay(block = tomorrowBlock, hours = emptyList())
        val forecast = makeForecast(days = listOf(tomorrowDay))

        forecast.blockForPeriod(ForecastPeriod.Tomorrow) shouldBe tomorrowBlock
    }

    @Test
    fun tomorrowReturnsNullWhenNoDays() {
        val forecast = makeForecast(days = emptyList())

        forecast.blockForPeriod(ForecastPeriod.Tomorrow) shouldBe null
    }
}

class ForecastScoreForBlockTest {
    @Test
    fun currentBlockReturnsCurrentScore() {
        val currentBlock = makeBlock(Instant.fromEpochSeconds(1000))
        val currentScore = makeScore(ScoreResult.Yes)
        val forecast = makeForecast(currentBlock = currentBlock)
        val score = makeScoreData(currentScore = currentScore)

        forecast.scoreForBlock(currentBlock, score) shouldBe currentScore
    }

    @Test
    fun todayBlockReturnsTodayScore() {
        val todayBlock = makeBlock(Instant.fromEpochSeconds(2000))
        val todayScore = makeScore(ScoreResult.Maybe)
        val forecast = makeForecast(todayBlock = todayBlock)
        val score = makeScoreData(todayScore = todayScore)

        forecast.scoreForBlock(todayBlock, score) shouldBe todayScore
    }

    @Test
    fun hourBlockReturnsMatchingHourScore() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val hour1 = makeBlock(Instant.fromEpochSeconds(7200))
        val score0 = makeScore(ScoreResult.Yes)
        val score1 = makeScore(ScoreResult.No)
        val forecast = makeForecast(todayHours = listOf(hour0, hour1))
        val score = makeScoreData(hourScores = listOf(score0, score1))

        forecast.scoreForBlock(hour1, score) shouldBe score1
    }

    @Test
    fun tomorrowBlockReturnsTomorrowScore() {
        val tomorrowBlock = makeBlock(Instant.fromEpochSeconds(86400))
        val tomorrowScore = makeScore(ScoreResult.No)
        val forecast = makeForecast(
            days = listOf(ForecastDay(block = tomorrowBlock, hours = emptyList())),
        )
        val score = makeScoreData(dayScores = listOf(tomorrowScore))

        forecast.scoreForBlock(tomorrowBlock, score) shouldBe tomorrowScore
    }

    @Test
    fun hourBlockWithoutMatchingScoreReturnsNull() {
        val hour0 = makeBlock(Instant.fromEpochSeconds(3600))
        val forecast = makeForecast(todayHours = listOf(hour0))
        val score = makeScoreData(hourScores = emptyList())

        forecast.scoreForBlock(hour0, score) shouldBe null
    }

    @Test
    fun unknownBlockReturnsNull() {
        val unknownBlock = makeBlock(Instant.fromEpochSeconds(99999))
        val forecast = makeForecast()
        val score = makeScoreData()

        forecast.scoreForBlock(unknownBlock, score) shouldBe null
    }
}
