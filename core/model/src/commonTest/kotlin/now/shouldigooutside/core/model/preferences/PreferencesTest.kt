package now.shouldigooutside.core.model.preferences

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.forecast.AirQuality
import kotlin.test.Test

class PreferencesTest {
    @Test
    fun default_minTemperature_is5() {
        Preferences.default.minTemperature shouldBe 5
    }

    @Test
    fun default_maxTemperature_is35() {
        Preferences.default.maxTemperature shouldBe 35
    }

    @Test
    fun default_windSpeed_is30() {
        Preferences.default.windSpeed shouldBe 30
    }

    @Test
    fun default_rain_isFalse() {
        Preferences.default.rain shouldBe false
    }

    @Test
    fun default_snow_isFalse() {
        Preferences.default.snow shouldBe false
    }

    @Test
    fun default_maxAqi_is3() {
        Preferences.default.maxAqi shouldBe AirQuality(3)
    }

    @Test
    fun default_includeApparentTemperature_isFalse() {
        Preferences.default.includeApparentTemperature shouldBe false
    }

    @Test
    fun defaultForGeneral_returnsDefaultPreferences() {
        Preferences.defaultFor(Activity.General) shouldBe Preferences.default
    }

    @Test
    fun defaultForWalking_hasCorrectMinTemperature() {
        Preferences.defaultFor(Activity.Walking).minTemperature shouldBe -10
    }

    @Test
    fun defaultForWalking_hasCorrectMaxTemperature() {
        Preferences.defaultFor(Activity.Walking).maxTemperature shouldBe 30
    }

    @Test
    fun defaultForWalking_rainIsTrue() {
        Preferences.defaultFor(Activity.Walking).rain shouldBe true
    }

    @Test
    fun defaultForWalking_snowIsTrue() {
        Preferences.defaultFor(Activity.Walking).snow shouldBe true
    }

    @Test
    fun defaultForRunning_hasCorrectMinTemperature() {
        Preferences.defaultFor(Activity.Running).minTemperature shouldBe 10
    }

    @Test
    fun defaultForRunning_rainIsFalse() {
        Preferences.defaultFor(Activity.Running).rain shouldBe false
    }

    @Test
    fun defaultForRunning_maxAqi_is2() {
        Preferences.defaultFor(Activity.Running).maxAqi shouldBe AirQuality(2)
    }

    @Test
    fun defaultForCycling_matchesRunningPreferences() {
        val cycling = Preferences.defaultFor(Activity.Cycling)
        val running = Preferences.defaultFor(Activity.Running)
        cycling shouldBe running
    }

    @Test
    fun defaultForHiking_rainIsTrue() {
        Preferences.defaultFor(Activity.Hiking).rain shouldBe true
    }

    @Test
    fun defaultForHiking_snowIsTrue() {
        Preferences.defaultFor(Activity.Hiking).snow shouldBe true
    }

    @Test
    fun defaultForSwimming_minTemperature_is20() {
        Preferences.defaultFor(Activity.Swimming).minTemperature shouldBe 20
    }

    @Test
    fun defaultForSwimming_maxAqi_is6() {
        Preferences.defaultFor(Activity.Swimming).maxAqi shouldBe AirQuality(6)
    }

    @Test
    fun defaultForCustom_returnsDefaultPreferences() {
        Preferences.defaultFor(Activity.Custom("My Activity")) shouldBe Preferences.default
    }
}
