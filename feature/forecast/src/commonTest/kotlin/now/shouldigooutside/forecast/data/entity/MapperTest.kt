package now.shouldigooutside.forecast.data.entity

import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.PrecipitationType
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastBlock
import now.shouldigooutside.test.testForecastDay
import now.shouldigooutside.test.testLocation
import kotlin.test.Test
import kotlin.time.Instant

class MapperTest {
    @Test
    fun roundTrip_toEntityThenToModel_preservesAllFields() {
        val forecast = testForecast(
            location = testLocation(latitude = 51.5, longitude = -0.1, name = "London"),
            current = testForecastBlock(instant = Instant.fromEpochSeconds(1000)),
            today = testForecastDay(
                block = testForecastBlock(instant = Instant.fromEpochSeconds(2000)),
                hours = listOf(testForecastBlock(instant = Instant.fromEpochSeconds(3000))),
            ),
            days = listOf(
                testForecastDay(block = testForecastBlock(instant = Instant.fromEpochSeconds(4000))),
            ),
            alerts = persistentListOf(Alert("Storm Warning", "Heavy rain expected")),
            instant = Instant.fromEpochMilliseconds(5000),
        )

        val result = forecast.toEntity().toModel()

        result shouldBe forecast
    }

    @Test
    fun toModel_unknownPrecipType_isDropped() {
        val entity = buildForecastEntity(
            precipTypes = listOf("Rain", "unknown_type", "Snow"),
        )

        val result = entity.toModel()

        result.current.precipitation.types shouldBe setOf(
            PrecipitationType.Rain,
            PrecipitationType.Snow,
        )
    }

    @Test
    fun toModel_unknownSevereWeatherRisk_defaultsToLow() {
        val entity = buildForecastEntity(severeWeatherRisk = "INVALID_RISK")

        val result = entity.toModel()

        result.current.severeWeatherRisk shouldBe SevereWeatherRisk.Low
    }

    @Test
    fun toModel_allPrecipTypesMapped() {
        val entity = buildForecastEntity(
            precipTypes = listOf("Rain", "Snow", "FreezingRain", "Hail"),
        )

        val result = entity.toModel()

        result.current.precipitation.types shouldBe setOf(
            PrecipitationType.Rain,
            PrecipitationType.Snow,
            PrecipitationType.FreezingRain,
            PrecipitationType.Hail,
        )
    }

    @Test
    fun toModel_allSevereWeatherRisksMapped() {
        val riskPairs = listOf(
            "None" to SevereWeatherRisk.None,
            "Low" to SevereWeatherRisk.Low,
            "Moderate" to SevereWeatherRisk.Moderate,
            "High" to SevereWeatherRisk.High,
        )

        riskPairs.forEach { (name, expected) ->
            val entity = buildForecastEntity(severeWeatherRisk = name)
            entity.toModel().current.severeWeatherRisk shouldBe expected
        }
    }

    @Test
    fun toModel_preservesAlerts() {
        val entity = buildForecastEntity(
            alerts = listOf(
                AlertEntity("Alert 1", "Description 1"),
                AlertEntity("Alert 2", "Description 2"),
            ),
        )

        val result = entity.toModel()

        result.alerts shouldBe listOf(
            Alert("Alert 1", "Description 1"),
            Alert("Alert 2", "Description 2"),
        )
    }

    @Test
    fun toModel_preservesAllAlertFields() {
        val entity = buildForecastEntity(
            alerts = listOf(
                AlertEntity(
                    title = "Storm",
                    description = "heavy rain",
                    event = "rainfall",
                    headline = "yellow warning - rainfall - in effect",
                    onsetEpoch = 1_700_000_000L,
                    endsEpoch = 1_700_010_000L,
                    link = "https://example.com/alert/1",
                    id = "abc-123",
                ),
            ),
        )

        val result = entity.toModel()

        result.alerts shouldBe listOf(
            Alert(
                title = "Storm",
                description = "heavy rain",
                event = "rainfall",
                headline = "yellow warning - rainfall - in effect",
                onset = Instant.fromEpochSeconds(1_700_000_000L),
                ends = Instant.fromEpochSeconds(1_700_010_000L),
                link = "https://example.com/alert/1",
                id = "abc-123",
            ),
        )
    }

    @Test
    fun roundTrip_alertWithAllFields_preserved() {
        val alert = Alert(
            title = "Storm",
            description = "heavy rain",
            event = "rainfall",
            headline = "yellow warning - rainfall - in effect",
            onset = Instant.fromEpochSeconds(1_700_000_000L),
            ends = Instant.fromEpochSeconds(1_700_010_000L),
            link = "https://example.com/alert/1",
            id = "abc-123",
        )

        val forecast = testForecast(alerts = persistentListOf(alert))

        forecast.toEntity().toModel() shouldBe forecast
    }

    @Test
    fun toModel_preservesLocation() {
        val entity = buildForecastEntity(
            lat = 48.8566,
            lon = 2.3522,
            name = "Paris",
            administrativeArea = "Ile-de-France",
            country = "France",
        )

        val result = entity.toModel()

        result.location.latitude shouldBe 48.8566
        result.location.longitude shouldBe 2.3522
        result.location.name shouldBe "Paris"
        result.location.administrativeArea shouldBe "Ile-de-France"
        result.location.country shouldBe "France"
    }

    @Test
    fun toModel_preservesInstant() {
        val epochMs = 1_700_000_000_000L
        val entity = buildForecastEntity(updatedAt = epochMs)

        val result = entity.toModel()

        result.instant shouldBe Instant.fromEpochMilliseconds(epochMs)
    }

    private fun buildForecastBlock(
        precipTypes: List<String> = emptyList(),
        severeWeatherRisk: String = "None",
    ): ForecastBlockEntity =
        ForecastBlockEntity(
            instant = 0L,
            humidity = 50.0,
            cloudCoverPercent = 20,
            temperature = TemperatureEntity(value = 20.0, feelsLike = 20.0, max = 25.0, min = 15.0),
            precipitation = PrecipitationEntity(amount = 0.0, probability = 0, types = precipTypes),
            wind = WindEntity(
                speed = 10.0,
                gust = 12.0,
                directionDegree = 180.0,
                maxSpeed = 12.0,
                meanSpeed = 10.0,
                minSpeed = 8.0,
            ),
            pressure = 1013.0,
            uvIndex = 3,
            visibility = 10.0,
            airQuality = 0,
            severeWeatherRisk = severeWeatherRisk,
        )

    private fun buildForecastEntity(
        lat: Double = 43.6532,
        lon: Double = -79.3832,
        name: String = "Toronto",
        administrativeArea: String? = "Ontario",
        country: String? = "Canada",
        precipTypes: List<String> = emptyList(),
        severeWeatherRisk: String = "None",
        alerts: List<AlertEntity> = emptyList(),
        updatedAt: Long = 0L,
    ): ForecastEntity =
        ForecastEntity(
            locationLat = lat,
            locationLong = lon,
            locationName = name,
            locationAdministrativeArea = administrativeArea,
            locationCountry = country,
            current = buildForecastBlock(precipTypes = precipTypes, severeWeatherRisk = severeWeatherRisk),
            today = ForecastDayEntity(block = buildForecastBlock(), hours = emptyList()),
            daily = emptyList(),
            alerts = alerts,
            updatedAt = updatedAt,
        )
}
