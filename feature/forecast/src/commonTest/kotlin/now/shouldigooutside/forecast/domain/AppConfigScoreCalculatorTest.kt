package now.shouldigooutside.forecast.domain

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.config.model.AppConfig
import now.shouldigooutside.core.config.model.PrecipitationConfig
import now.shouldigooutside.core.domain.forecast.DefaultScoreCalculator
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.test.FakeAppConfigRepo
import now.shouldigooutside.test.testForecast
import kotlin.test.Test

class AppConfigScoreCalculatorTest {
    @Test
    fun delegatesToDefaultScoreCalculator() {
        val appConfigRepo = FakeAppConfigRepo()
        val calculator = AppConfigScoreCalculator(appConfigRepo)
        val forecast = testForecast()

        val result = calculator.calculate(forecast, Preferences.default, includeAirQuality = true)

        val expected = DefaultScoreCalculator().calculate(
            forecast,
            Preferences.default,
            includeAirQuality = true,
        )
        result shouldBe expected
    }

    @Test
    fun usesConfigPrecipitationValues() {
        val customConfig = AppConfig(
            precipitation = PrecipitationConfig(
                maxChance = 0.5f,
                lowAmountMm = 1,
                moderateAmountMm = 5,
            ),
            scoreNearPercent = 0.2f,
            scoreMaxNearReasons = 1,
        )
        val appConfigRepo = FakeAppConfigRepo(customConfig)
        val calculator = AppConfigScoreCalculator(appConfigRepo)
        val forecast = testForecast()

        val result = calculator.calculate(forecast, Preferences.default, includeAirQuality = false)

        val expected = DefaultScoreCalculator(
            maxChance = 0.5f,
            lowAmountMm = 1,
            moderateAmountMm = 5,
            nearPercent = 0.2f,
            maxNearReasons = 1,
        ).calculate(forecast, Preferences.default, includeAirQuality = false)
        result shouldBe expected
    }
}
