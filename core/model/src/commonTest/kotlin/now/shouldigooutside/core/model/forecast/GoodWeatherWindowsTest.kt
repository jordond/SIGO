package now.shouldigooutside.core.model.forecast

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class GoodWeatherWindowsTest {
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
        windows[0].end shouldBe hour(4)
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
        windows[0].end shouldBe hour(5)
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
        windows[2].end shouldBe hour(7)
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
        windows[1].end shouldBe hour(5)
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
        windows[0].end shouldBe hour(3)
    }

    @Test
    fun singleHourYes() {
        val forecast = forecast(hourCount = 1)
        val score = forecastScore(listOf(yes))

        val windows = forecast.goodWeatherWindows(score)

        windows shouldHaveSize 1
        windows[0].start shouldBe hour(0)
        windows[0].end shouldBe hour(1)
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
}
