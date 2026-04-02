package now.shouldigooutside.settings.domain

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.preferences.PreferenceRanges
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.test.FakeSettingsRepo
import kotlin.test.Test
import kotlin.time.Instant

class DefaultGetPreferenceRangesUseCaseTest {
    private val settingsRepo = FakeSettingsRepo(
        initial = Settings(firstLaunch = Instant.fromEpochSeconds(0), units = Units.Metric),
    )

    private val useCase = DefaultGetPreferenceRangesUseCase(settingsRepo = settingsRepo)

    @Test
    fun rangesFunctionReturnsCurrentUnitRanges() {
        val expected = PreferenceRanges.from(Units.Metric)

        val result = useCase.ranges()

        result shouldBe expected
    }

    @Test
    fun rangesFlowEmitsOnUnitChange() =
        runTest {
            useCase.ranges.test {
                awaitItem() // initial Metric emission

                settingsRepo.update { it.copy(units = Units.Imperial) }

                val updated = awaitItem()
                updated shouldBe PreferenceRanges.from(Units.Imperial)
                cancel()
            }
        }

    @Test
    fun rangesFlowDoesNotEmitOnNonUnitChange() =
        runTest {
            useCase.ranges.test {
                awaitItem() // initial emission

                settingsRepo.update { it.copy(themeMode = ThemeMode.Dark) }

                expectNoEvents()
                cancel()
            }
        }

    @Test
    fun rangesFlowEmitsDistinctOnly() =
        runTest {
            useCase.ranges.test {
                awaitItem() // first emission for Metric

                // Update to the same units — should not re-emit
                settingsRepo.update { it.copy(units = Units.Metric) }

                expectNoEvents()
                cancel()
            }
        }

    @Test
    fun rangesImperialHasFahrenheitTemperature() {
        settingsRepo.update { it.copy(units = Units.Imperial) }

        val result = useCase.ranges()

        // -30°C = -22°F, 40°C = 104°F
        val expected = PreferenceRanges.from(Units.Imperial)
        result shouldBe expected
        // Spot-check: temperature range start should be in Fahrenheit territory (below 0°C in Celsius)
        (result.temperature.start < 0f) shouldBe true
    }
}
