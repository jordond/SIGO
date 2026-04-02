package now.shouldigooutside.forecast.data.entity

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ForecastRequestQueryTest {
    @Test
    fun toLocationWithName() {
        val query = ForecastRequestQuery(lat = 43.65, lon = -79.38, name = "Toronto")

        val location = query.toLocation()

        location.latitude shouldBe 43.65
        location.longitude shouldBe -79.38
        location.name shouldBe "Toronto"
    }

    @Test
    fun toLocationWithoutNameUsesCoordinates() {
        val query = ForecastRequestQuery(lat = 51.5, lon = -0.1)

        val location = query.toLocation()

        location.latitude shouldBe 51.5
        location.longitude shouldBe -0.1
        location.name shouldBe "51.5,-0.1"
    }
}
