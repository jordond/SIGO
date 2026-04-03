package now.shouldigooutside.forecast.data.source.visualcrossing

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.test.FakeNowProvider
import now.shouldigooutside.test.testLocation
import kotlin.test.Test

class VisualCrossingForecastSourceTest {
    @Test
    fun forecastFor_location_usesCoordinateApi_logsCost_andCapsDaysAtFive() =
        runTest {
            val api = FakeVisualCrossingApi(response = buildResponse(dayCount = 7, queryCost = 3))
            val source = VisualCrossingForecastSource(api::recordCost, api, FakeNowProvider())

            val result = source.forecastFor(testLocation(latitude = 43.0, longitude = -79.0))

            api.lastLatitude shouldBe 43.0
            api.lastLongitude shouldBe -79.0
            api.loggedCosts shouldBe listOf(3)
            result.days.size shouldBe 5
        }

    @Test
    fun forecastFor_string_usesNameApi_andLogsCost() =
        runTest {
            val api = FakeVisualCrossingApi(response = buildResponse(dayCount = 2, queryCost = 7))
            val source = VisualCrossingForecastSource(api::recordCost, api, FakeNowProvider())

            val result = source.forecastFor("Toronto")

            api.lastQuery shouldBe "Toronto"
            api.loggedCosts shouldBe listOf(7)
            result.location.name shouldBe "Toronto"
        }
}

private class FakeVisualCrossingApi(
    private val response: VCForecastResponse,
) : VisualCrossingApi {
    var lastLatitude: Double? = null
    var lastLongitude: Double? = null
    var lastQuery: String? = null
    val loggedCosts = mutableListOf<Int>()

    fun recordCost(cost: Int) {
        loggedCosts += cost
    }

    override suspend fun forecastFor(
        latitude: Double,
        longitude: Double,
    ): VCForecastResponse {
        lastLatitude = latitude
        lastLongitude = longitude
        return response
    }

    override suspend fun forecastFor(name: String): VCForecastResponse {
        lastQuery = name
        return response
    }
}

private fun buildResponse(
    dayCount: Int,
    queryCost: Int,
): VCForecastResponse {
    val days = (0 until dayCount).map { index ->
        VCForecastBlock(
            datetime = "2024-01-${index + 1}",
            datetimeEpoch = index * 86_400L,
            temp = 20.0 + index,
            hours = emptyList(),
        )
    }

    return VCForecastResponse(
        queryCost = queryCost,
        latitude = 43.0,
        longitude = -79.0,
        resolvedAddress = "Toronto, ON",
        address = "Toronto",
        timezone = "America/Toronto",
        days = days,
        alerts = emptyList(),
        currentConditions = days.first(),
    )
}
