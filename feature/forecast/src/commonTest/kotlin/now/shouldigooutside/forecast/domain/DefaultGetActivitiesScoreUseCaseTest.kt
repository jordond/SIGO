package now.shouldigooutside.forecast.domain

import app.cash.turbine.test
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.test.FakeForecastStateHolder
import now.shouldigooutside.test.FakeScoreCalculator
import now.shouldigooutside.test.FakeSettingsRepo
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastScore
import now.shouldigooutside.test.testScore
import kotlin.test.Test
import kotlin.time.Instant

class DefaultGetActivitiesScoreUseCaseTest {
    private val settingsRepo = FakeSettingsRepo()
    private val forecastHolder = FakeForecastStateHolder()
    private val scoreCalculator = FakeScoreCalculator()

    private val useCase = DefaultGetActivitiesScoreUseCase(
        settingsRepo = settingsRepo,
        forecastHolder = forecastHolder,
        scoreCalculator = scoreCalculator,
    )

    @Test
    fun scoresReturnsEmptyWhenForecastIsLoading() {
        forecastHolder.emit(AsyncResult.Loading)

        val result = useCase.scores()

        result.shouldBeEmpty()
    }

    @Test
    fun scoresReturnsEmptyWhenForecastIsError() {
        forecastHolder.emit(AsyncResult.Error(RuntimeException("fail")))

        val result = useCase.scores()

        result.shouldBeEmpty()
    }

    @Test
    fun scoresReturnsOneEntryPerActivity() {
        val forecast = testForecast()
        forecastHolder.emit(AsyncResult.Success(forecast))

        val runningPrefs = Preferences.defaultFor(Activity.Running)
        settingsRepo.update {
            it.add(Activity.Running, runningPrefs)
        }

        val result = useCase.scores()

        result shouldHaveSize 2 // General + Running
        result.any { it.activity == Activity.General } shouldBe true
        result.any { it.activity == Activity.Running } shouldBe true
    }

    @Test
    fun scoresPassesIncludeAirQualityToCalculator() {
        val forecast = testForecast()
        forecastHolder.emit(AsyncResult.Success(forecast))
        settingsRepo.update { it.copy(includeAirQuality = false) }

        useCase.scores()

        scoreCalculator.lastIncludeAirQuality shouldBe false
    }

    @Test
    fun scoresPassesCorrectPreferencesPerActivity() {
        val forecast = testForecast()
        forecastHolder.emit(AsyncResult.Success(forecast))

        val activities = persistentMapOf(
            Activity.General to Preferences.default,
            Activity.Running to Preferences.defaultFor(Activity.Running),
        )
        settingsRepo.update {
            Settings(
                firstLaunch = Instant.fromEpochSeconds(0),
                activities = activities,
            )
        }

        val result = useCase.scores()

        val runningScore = result.first { it.activity == Activity.Running }
        runningScore.preferences shouldBe Preferences.defaultFor(Activity.Running)
    }

    @Test
    fun scoresFlowEmitsOnActivitiesChange() =
        runTest {
            forecastHolder.emit(AsyncResult.Success(testForecast()))

            useCase.scoresFlow().test {
                awaitItem() // initial emission

                settingsRepo.update {
                    it.add(Activity.Running, Preferences.defaultFor(Activity.Running))
                }

                val updated = awaitItem()
                updated shouldHaveSize 2
                cancel()
            }
        }

    @Test
    fun scoresFlowEmitsOnAirQualityToggle() =
        runTest {
            forecastHolder.emit(AsyncResult.Success(testForecast()))

            useCase.scoresFlow().test {
                awaitItem() // initial emission

                // Change the result so distinctUntilChanged lets the new emission through
                scoreCalculator.result = testForecastScore(
                    current = testScore(result = ScoreResult.No),
                )
                settingsRepo.update { it.copy(includeAirQuality = !it.includeAirQuality) }

                awaitItem() // re-emission after toggle
                cancel()
            }
        }

    @Test
    fun scoresFlowDoesNotEmitOnIrrelevantSettingsChange() =
        runTest {
            forecastHolder.emit(AsyncResult.Success(testForecast()))

            useCase.scoresFlow().test {
                awaitItem() // initial emission

                settingsRepo.update { it.copy(themeMode = ThemeMode.Dark) }

                expectNoEvents()
                cancel()
            }
        }

    @Test
    fun scoresFlowEmitsOnForecastChange() =
        runTest {
            forecastHolder.emit(AsyncResult.Success(testForecast()))

            useCase.scoresFlow().test {
                awaitItem() // initial emission

                // Change the result so distinctUntilChanged lets the new emission through
                scoreCalculator.result = testForecastScore(
                    current = testScore(result = ScoreResult.No),
                )
                forecastHolder.emit(
                    AsyncResult.Success(testForecast(instant = Instant.fromEpochSeconds(9999))),
                )

                awaitItem() // re-emission with new forecast
                cancel()
            }
        }

    @Test
    fun scoresUsesCorrectScore() {
        val expectedScore = testForecastScore()
        scoreCalculator.result = expectedScore
        forecastHolder.emit(AsyncResult.Success(testForecast()))

        val result = useCase.scores()

        result.first().score shouldBe expectedScore
    }
}
