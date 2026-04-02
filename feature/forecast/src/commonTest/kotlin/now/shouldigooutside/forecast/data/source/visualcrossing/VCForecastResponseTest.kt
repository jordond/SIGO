package now.shouldigooutside.forecast.data.source.visualcrossing

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.forecast.PrecipitationType
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.test.FakeNowProvider
import kotlin.test.Test
import kotlin.time.Instant

class VCForecastResponseTest {
    @Test
    fun precipType_rain_mappedToRain() {
        val response = buildResponse(precipType = listOf("rain"))

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.precipitation.types shouldBe setOf(PrecipitationType.Rain)
    }

    @Test
    fun precipType_snow_mappedToSnow() {
        val response = buildResponse(precipType = listOf("snow"))

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.precipitation.types shouldBe setOf(PrecipitationType.Snow)
    }

    @Test
    fun precipType_freezingrain_mappedToFreezingRain() {
        val response = buildResponse(precipType = listOf("freezingrain"))

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.precipitation.types shouldBe setOf(PrecipitationType.FreezingRain)
    }

    @Test
    fun precipType_ice_mappedToHail() {
        val response = buildResponse(precipType = listOf("ice"))

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.precipitation.types shouldBe setOf(PrecipitationType.Hail)
    }

    @Test
    fun precipType_unknown_isDropped() {
        val response = buildResponse(precipType = listOf("rain", "tornado", "ice"))

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.precipitation.types shouldBe setOf(
            PrecipitationType.Rain,
            PrecipitationType.Hail,
        )
    }

    @Test
    fun precipType_null_resultsInEmptySet() {
        val response = buildResponse(precipType = null)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.precipitation.types shouldBe emptySet()
    }

    @Test
    fun severeRisk_null_mapsToNone() {
        val response = buildResponse(severeRisk = null)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.severeWeatherRisk shouldBe SevereWeatherRisk.None
    }

    @Test
    fun severeRisk_30_mapsToLow() {
        val response = buildResponse(severeRisk = 30.0)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.severeWeatherRisk shouldBe SevereWeatherRisk.Low
    }

    @Test
    fun severeRisk_31_mapsToModerate() {
        val response = buildResponse(severeRisk = 31.0)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.severeWeatherRisk shouldBe SevereWeatherRisk.Moderate
    }

    @Test
    fun severeRisk_70_mapsToModerate() {
        val response = buildResponse(severeRisk = 70.0)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.severeWeatherRisk shouldBe SevereWeatherRisk.Moderate
    }

    @Test
    fun severeRisk_71_mapsToHigh() {
        val response = buildResponse(severeRisk = 71.0)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.severeWeatherRisk shouldBe SevereWeatherRisk.High
    }

    @Test
    fun aqi_null_mapsTo0() {
        val response = buildResponse(aqiUs = null)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.airQuality.value shouldBe 0
    }

    @Test
    fun aqi_50_mapsTo2() {
        val response = buildResponse(aqiUs = 50.0)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.airQuality.value shouldBe 2
    }

    @Test
    fun aqi_150_mapsTo6() {
        val response = buildResponse(aqiUs = 150.0)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.airQuality.value shouldBe 6
    }

    @Test
    fun aqi_301_mapsTo11() {
        val response = buildResponse(aqiUs = 301.0)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.block.airQuality.value shouldBe 11
    }

    @Test
    fun hourFiltering_filtersFromNowEpoch() {
        // now = epoch 7200, hours at 3600, 7200, 10800, 14400, 18000, 21600
        val nowProvider = FakeNowProvider(instant = Instant.fromEpochSeconds(7200))
        val hours = listOf(
            3600L,
            7200L,
            10800L,
            14400L,
            18000L,
            21600L,
        ).map { buildBlock(datetimeEpoch = it) }
        val response = buildResponse(todayHours = hours)

        val model = response.toModel(nowProvider, maxDays = 3)

        model.today.hours.size shouldBe 5
        model.today.hours
            .first()
            .instant shouldBe Instant.fromEpochSeconds(7200)
    }

    @Test
    fun hourFiltering_clampsWhenAllHoursPast() {
        val nowProvider = FakeNowProvider(instant = Instant.fromEpochSeconds(99999))
        val hours = listOf(1000L, 2000L, 3000L).map { buildBlock(datetimeEpoch = it) }
        val response = buildResponse(todayHours = hours)

        val model = response.toModel(nowProvider, maxDays = 3)

        // All hours are in the past: falls back to startIndex=0, takes up to 5
        model.today.hours.size shouldBe 3
    }

    @Test
    fun hourFiltering_emptyHours_producesEmptyList() {
        val response = buildResponse(todayHours = emptyList())

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        model.today.hours shouldBe emptyList()
    }

    @Test
    fun hourFiltering_capsAtFiveHours() {
        val nowProvider = FakeNowProvider(instant = Instant.fromEpochSeconds(0))
        val hours = (1..10).map { buildBlock(datetimeEpoch = it.toLong() * 3600) }
        val response = buildResponse(todayHours = hours)

        val model = response.toModel(nowProvider, maxDays = 3)

        model.today.hours.size shouldBe 5
    }

    @Test
    fun structure_firstDayBecomesToday() {
        val todayBlock = buildBlock(datetimeEpoch = 1000, temp = 21.0)
        val day2 = buildBlock(datetimeEpoch = 2000, temp = 22.0)
        val response = buildResponseWithDays(days = listOf(todayBlock, day2))

        val model = response.toModel(FakeNowProvider(), maxDays = 5)

        model.today.block.temperature.value shouldBe 21.0
        model.days
            .first()
            .block.temperature.value shouldBe 22.0
    }

    @Test
    fun structure_respectsMaxDays() {
        val days = (0..6).map { buildBlock(datetimeEpoch = it.toLong() * 86400) }
        val response = buildResponseWithDays(days = days)

        val model = response.toModel(FakeNowProvider(), maxDays = 3)

        // today is days[0], remaining are days[1..6] capped to maxDays=3
        model.days.size shouldBe 3
    }

    private fun buildBlock(
        datetimeEpoch: Long = 0L,
        temp: Double? = 20.0,
        precipType: List<String>? = null,
        severeRisk: Double? = null,
        aqiUs: Double? = null,
        hours: List<VCForecastBlock>? = null,
    ): VCForecastBlock =
        VCForecastBlock(
            datetime = "2024-01-01",
            datetimeEpoch = datetimeEpoch,
            temp = temp,
            precipType = precipType,
            severeRisk = severeRisk,
            aqiUs = aqiUs,
            hours = hours,
        )

    private fun buildResponse(
        precipType: List<String>? = null,
        severeRisk: Double? = null,
        aqiUs: Double? = null,
        todayHours: List<VCForecastBlock>? = null,
    ): VCForecastResponse {
        val todayBlock = buildBlock(
            datetimeEpoch = 0L,
            precipType = precipType,
            severeRisk = severeRisk,
            aqiUs = aqiUs,
            hours = todayHours,
        )
        return VCForecastResponse(
            queryCost = 1,
            latitude = 43.0,
            longitude = -79.0,
            resolvedAddress = "Toronto, ON",
            address = "Toronto",
            timezone = "America/Toronto",
            days = listOf(todayBlock, buildBlock(datetimeEpoch = 86400)),
            alerts = emptyList(),
            currentConditions = buildBlock(datetimeEpoch = 0L),
        )
    }

    private fun buildResponseWithDays(days: List<VCForecastBlock>): VCForecastResponse =
        VCForecastResponse(
            queryCost = 1,
            latitude = 43.0,
            longitude = -79.0,
            resolvedAddress = "Toronto, ON",
            address = "Toronto",
            timezone = "America/Toronto",
            days = days,
            alerts = emptyList(),
            currentConditions = buildBlock(),
        )
}
