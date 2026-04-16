package now.shouldigooutside.core.model.forecast

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.score.dominantReason
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class WeatherBannerInfoTest {
    private val activity = Activity.General

    @Test
    fun nullScoreReturnsNull() {
        val forecast = forecast(hourCount = 3)
        forecast.weatherBannerInfo(
            score = null,
            currentResult = ScoreResult.Yes,
            activity = activity,
            now = hour(0),
        ) shouldBe null
    }

    @Test
    fun nullCurrentResultReturnsNull() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(listOf(yes, yes, yes))
        forecast.weatherBannerInfo(
            score = score,
            currentResult = null,
            activity = activity,
            now = hour(0),
        ) shouldBe null
    }

    @Test
    fun yesWithTransitionToNoReturnsGoNow() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(yes, yes, yes, no, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.Yes,
            activity = activity,
            now = hour(0),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.GoNow>()
        info.endsAt shouldBe hour(3)
        info.activity shouldBe activity
    }

    @Test
    fun yesAllRemainingReturnsGoNowEndOfDay() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(listOf(yes, yes, yes))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.Yes,
            activity = activity,
            now = hour(0),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.GoNow>()
        info.endsAt shouldBe hour(2) + 1.hours
        info.reason shouldBe null
    }

    @Test
    fun yesWithNowPastAllHoursReturnsGoNowEndOfDay() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(listOf(yes, yes, yes))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.Yes,
            activity = activity,
            now = hour(10),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.GoNow>()
        info.endsAt shouldBe hour(2) + 1.hours
    }

    @Test
    fun noWithFutureYesWindowReturnsNextWindow() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(no, no, yes, yes, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.No,
            activity = activity,
            now = hour(0),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.NextWindow>()
        info.window.start shouldBe hour(2)
        info.window.end shouldBe hour(4)
        info.quality shouldBe WindowQuality.Good
    }

    @Test
    fun noWithNoYesOrMaybeWindowReturnsNoWindowToday() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(listOf(no, no, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.No,
            activity = activity,
            now = hour(0),
        )

        info shouldBe WeatherBannerInfo.NoWindowToday
    }

    @Test
    fun maybeTreatedAsNoReturnsNextWindow() {
        val forecast = forecast(hourCount = 4)
        val score = forecastScore(listOf(maybe, yes, yes, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.Maybe,
            activity = activity,
            now = hour(0),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.NextWindow>()
        info.window.start shouldBe hour(1)
        info.window.end shouldBe hour(3)
        info.quality shouldBe WindowQuality.Good
    }

    @Test
    fun noWithOnlyMaybeWindowReturnsBorderlineNextWindow() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(no, maybe, maybe, maybe, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.No,
            activity = activity,
            now = hour(0),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.NextWindow>()
        info.window.start shouldBe hour(1)
        info.window.end shouldBe hour(4)
        info.quality shouldBe WindowQuality.Borderline
    }

    @Test
    fun goodWindowPreferredOverBorderline() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(no, maybe, no, yes, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.No,
            activity = activity,
            now = hour(0),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.NextWindow>()
        info.window.start shouldBe hour(3)
        info.quality shouldBe WindowQuality.Good
    }

    @Test
    fun nextWindowSurfacesWindowStartingAtNow() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(maybe, maybe, maybe, no, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.No,
            activity = activity,
            now = hour(0),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.NextWindow>()
        info.window.start shouldBe hour(0)
        info.quality shouldBe WindowQuality.Borderline
    }

    @Test
    fun nextWindowSurfacesOngoingWindow() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(yes, yes, yes, no, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.No,
            activity = activity,
            now = hour(1) + 30.minutes,
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.NextWindow>()
        info.window.start shouldBe hour(0)
        info.window.end shouldBe hour(3)
    }

    @Test
    fun maybeWithNoWindowReturnsNoWindowToday() {
        val forecast = forecast(hourCount = 3)
        val score = forecastScore(listOf(no, no, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.Maybe,
            activity = activity,
            now = hour(0),
        )

        info shouldBe WeatherBannerInfo.NoWindowToday
    }

    @Test
    fun nextWindowSkipsPastWindows() {
        val forecast = forecast(hourCount = 5)
        val score = forecastScore(listOf(yes, no, no, yes, yes))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.No,
            activity = activity,
            now = hour(2),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.NextWindow>()
        info.window.start shouldBe hour(3)
        info.quality shouldBe WindowQuality.Good
    }

    @Test
    fun goNowReasonReflectsTransitionDominantReason() {
        val rainTransition = Score(
            ScoreResult.No,
            EmptyReasons.copy(precipitation = ReasonValue.Outside),
        )
        val forecast = forecast(hourCount = 4)
        val score = forecastScore(listOf(yes, yes, rainTransition, no))

        val info = forecast.weatherBannerInfo(
            score = score,
            currentResult = ScoreResult.Yes,
            activity = activity,
            now = hour(0),
        )

        info.shouldBeInstanceOf<WeatherBannerInfo.GoNow>()
        info.reason shouldBe WeatherReason.Precipitation
    }
}

class DominantReasonTest {
    @Test
    fun singleOutsideReturnsThatReason() {
        val reasons = EmptyReasons.copy(precipitation = ReasonValue.Outside)
        reasons.dominantReason() shouldBe WeatherReason.Precipitation
    }

    @Test
    fun multipleOutsideReturnsHighestSeverity() {
        val reasons = EmptyReasons.copy(
            wind = ReasonValue.Outside,
            severeWeather = ReasonValue.Outside,
        )
        reasons.dominantReason() shouldBe WeatherReason.SevereWeather
    }

    @Test
    fun noOutsideSingleNearReturnsThatReason() {
        val reasons = EmptyReasons.copy(wind = ReasonValue.Near)
        reasons.dominantReason() shouldBe WeatherReason.Wind
    }

    @Test
    fun allInsideReturnsNull() {
        EmptyReasons.dominantReason() shouldBe null
    }

    @Test
    fun outsideBeatsNear() {
        val reasons = Reasons(
            wind = ReasonValue.Outside,
            temperature = ReasonValue.Inside,
            precipitation = ReasonValue.Inside,
            severeWeather = ReasonValue.Near,
            airQuality = ReasonValue.Inside,
        )
        reasons.dominantReason() shouldBe WeatherReason.Wind
    }
}
