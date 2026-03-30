package now.shouldigooutside.forecast.data.entity

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.preferences.Preferences
import kotlin.test.Test

class ForecastScoreRequestQueryTest {
    @Test
    fun allNull_usesDefaults() {
        val query = ForecastScoreRequestQuery(lat = 43.0, lon = -79.0)

        val (_, prefs) = query.toModels()

        prefs shouldBe Preferences.default
    }

    @Test
    fun allPresent_passesThrough() {
        val query = ForecastScoreRequestQuery(
            lat = 43.0,
            lon = -79.0,
            name = "Toronto",
            maxTemp = 30,
            minTemp = 5,
            maxWind = 25,
            allowRain = true,
            allowSnow = false,
            maxAqi = 4,
        )

        val (_, prefs) = query.toModels()

        prefs.maxTemperature shouldBe 30
        prefs.minTemperature shouldBe 5
        prefs.windSpeed shouldBe 25
        prefs.rain shouldBe true
        prefs.snow shouldBe false
        prefs.maxAqi shouldBe AirQuality(4)
    }

    @Test
    fun mixedNullAndNonNull_fallsBackToDefaultsForNulls() {
        val query = ForecastScoreRequestQuery(
            lat = 43.0,
            lon = -79.0,
            maxTemp = 28,
            allowRain = true,
        )

        val (_, prefs) = query.toModels()

        prefs.maxTemperature shouldBe 28
        prefs.rain shouldBe true
        prefs.minTemperature shouldBe Preferences.default.minTemperature
        prefs.windSpeed shouldBe Preferences.default.windSpeed
        prefs.snow shouldBe Preferences.default.snow
        prefs.maxAqi shouldBe Preferences.default.maxAqi
    }

    @Test
    fun locationCreatedCorrectly() {
        val query = ForecastScoreRequestQuery(lat = 48.8566, lon = 2.3522, name = "Paris")

        val (location, _) = query.toModels()

        location.latitude shouldBe 48.8566
        location.longitude shouldBe 2.3522
        location.name shouldBe "Paris"
    }

    @Test
    fun locationWithoutName_usesCoordinatesAsName() {
        val query = ForecastScoreRequestQuery(lat = 51.5, lon = -0.1)

        val (location, _) = query.toModels()

        location.name shouldBe "51.5,-0.1"
    }
}
