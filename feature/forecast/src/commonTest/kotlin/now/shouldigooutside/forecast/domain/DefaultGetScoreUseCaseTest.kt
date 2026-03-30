package now.shouldigooutside.forecast.domain

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.test.FakeScoreCalculator
import now.shouldigooutside.test.FakeSettingsRepo
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastScore
import kotlin.test.Test

class DefaultGetScoreUseCaseTest {
    private val fakeSettingsRepo = FakeSettingsRepo()
    private val fakeScoreCalculator = FakeScoreCalculator()

    private val useCase = DefaultGetScoreUseCase(
        settingsRepo = fakeSettingsRepo,
        scoreCalculator = fakeScoreCalculator,
    )

    @Test
    fun scoreForReturnsCalculatedScore() {
        val expectedScore = testForecastScore()
        fakeScoreCalculator.result = expectedScore
        val forecast = testForecast()

        val result = useCase.scoreFor(forecast)

        result shouldBe expectedScore
    }

    @Test
    fun scoreForUsesCurrentSettings() {
        val customPreferences = Preferences.default.copy(windSpeed = 99)
        fakeSettingsRepo.update { it.copy(includeAirQuality = false) }
        fakeSettingsRepo.update { settings ->
            settings.updatePreferences(settings.selectedActivity, customPreferences)
        }
        val forecast = testForecast()

        useCase.scoreFor(forecast)

        fakeScoreCalculator.lastPreferences shouldBe customPreferences
        fakeScoreCalculator.lastIncludeAirQuality shouldBe false
    }

    @Test
    fun scoreForFlowEmitsOnSettingsChange() =
        runTest {
            val forecast = testForecast()
            val updatedPreferences = Preferences.default.copy(windSpeed = 42)

            useCase.scoreForFlow(forecast).test {
                awaitItem() // initial emission

                fakeSettingsRepo.update { settings ->
                    settings.updatePreferences(settings.selectedActivity, updatedPreferences)
                }

                awaitItem() // re-emission after preferences change
                cancel()
            }
        }

    @Test
    fun scoreForFlowEmitsOnAirQualityToggle() =
        runTest {
            val forecast = testForecast()

            useCase.scoreForFlow(forecast).test {
                awaitItem() // initial emission

                fakeSettingsRepo.update { it.copy(includeAirQuality = !it.includeAirQuality) }

                awaitItem() // re-emission after air quality toggle
                cancel()
            }
        }

    @Test
    fun scoreForFlowDoesNotEmitOnIrrelevantSettingsChange() =
        runTest {
            val forecast = testForecast()

            useCase.scoreForFlow(forecast).test {
                awaitItem() // initial emission

                // Change a field that doesn't affect preferences or includeAirQuality
                fakeSettingsRepo.update { it.copy(themeMode = ThemeMode.Dark) }

                expectNoEvents()
                cancel()
            }
        }
}
