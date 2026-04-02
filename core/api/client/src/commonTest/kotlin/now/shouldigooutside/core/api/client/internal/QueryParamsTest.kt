package now.shouldigooutside.core.api.client.internal

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

@Serializable
private data class SimpleQuery(
    val lat: Double,
    val lon: Double,
    val name: String? = null,
)

@Serializable
private data class NestedQuery(
    val location: LocationQuery,
    val units: String,
)

@Serializable
private data class LocationQuery(
    val lat: Double,
    val lon: Double,
)

class QueryParamsTest {
    @Test
    fun flatObjectProducesTopLevelKeys() {
        val query = SimpleQuery(lat = 43.65, lon = -79.38)

        val result = query.toQueryParams()

        result["lat"] shouldBe "43.65"
        result["lon"] shouldBe "-79.38"
    }

    @Test
    fun nullFieldsAreOmitted() {
        val query = SimpleQuery(lat = 43.65, lon = -79.38, name = null)

        val result = query.toQueryParams()

        result.containsKey("name") shouldBe false
    }

    @Test
    fun stringFieldsAreIncluded() {
        val query = SimpleQuery(lat = 43.65, lon = -79.38, name = "Toronto")

        val result = query.toQueryParams()

        result["name"] shouldBe "Toronto"
    }

    @Test
    fun nestedObjectsUseDotNotation() {
        val query = NestedQuery(
            location = LocationQuery(lat = 43.65, lon = -79.38),
            units = "metric",
        )

        val result = query.toQueryParams()

        result["location.lat"] shouldBe "43.65"
        result["location.lon"] shouldBe "-79.38"
        result["units"] shouldBe "metric"
    }
}
