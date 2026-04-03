package now.shouldigooutside.core.model.preferences

import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.units.Units
import kotlin.test.Test

class PreferenceRangesTest {
    @Test
    fun metric_temperatureStart_isCelsiusDefault() {
        val ranges = PreferenceRanges.from(Units.Metric)
        // Default Celsius range starts at -30°C
        ranges.temperature.start shouldBe (-30.0f plusOrMinus 0.1f)
    }

    @Test
    fun metric_temperatureEnd_isCelsiusDefault() {
        val ranges = PreferenceRanges.from(Units.Metric)
        // Default Celsius range ends at 40°C
        ranges.temperature.endInclusive shouldBe (40.0f plusOrMinus 0.1f)
    }

    @Test
    fun imperial_temperatureStart_convertedToFahrenheit() {
        val ranges = PreferenceRanges.from(Units.Imperial)
        // -30°C = -22°F
        ranges.temperature.start shouldBe (-22.0f plusOrMinus 0.5f)
    }

    @Test
    fun imperial_temperatureEnd_convertedToFahrenheit() {
        val ranges = PreferenceRanges.from(Units.Imperial)
        // 40°C = 104°F
        ranges.temperature.endInclusive shouldBe (104.0f plusOrMinus 0.5f)
    }

    @Test
    fun imperial_windSpeed_convertedToMph() {
        val ranges = PreferenceRanges.from(Units.Imperial)
        // 50 kph ≈ 31.07 mph
        ranges.maxWindSpeed shouldBe (31.07f plusOrMinus 0.1f)
    }

    @Test
    fun si_temperatureStart_convertedToKelvin() {
        val ranges = PreferenceRanges.from(Units.SI)
        // -30°C = 243.15K
        ranges.temperature.start shouldBe (243.15f plusOrMinus 0.1f)
    }

    @Test
    fun si_temperatureEnd_convertedToKelvin() {
        val ranges = PreferenceRanges.from(Units.SI)
        // 40°C = 313.15K
        ranges.temperature.endInclusive shouldBe (313.15f plusOrMinus 0.1f)
    }

    @Test
    fun si_windSpeed_convertedToMps() {
        val ranges = PreferenceRanges.from(Units.SI)
        // 50 kph = 50/3.6 ≈ 13.89 m/s
        ranges.maxWindSpeed shouldBe (13.89f plusOrMinus 0.1f)
    }
}
