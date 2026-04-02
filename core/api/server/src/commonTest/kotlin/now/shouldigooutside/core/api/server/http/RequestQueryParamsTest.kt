package now.shouldigooutside.core.api.server.http

import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.server.exception.BadRequestException
import kotlin.test.Test
import kotlin.test.assertFailsWith

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

@Serializable
private data class TestQuery(
    val lat: Double,
    val lon: Double,
    val name: String? = null,
)

@Serializable
private data class QueryWithSerialName(
    @SerialName("max_temp")
    val maxTemp: Int,
)

private fun request(params: Map<String, String>): ServerRequest =
    ServerRequest(
        url = "https://api.example.com/test",
        method = HttpMethod.Get,
        headers = headersOf(),
        queryParameters = params,
    )

class RequestQueryParamsTest {
    @Test
    fun parsesRequiredFields() {
        val req = request(mapOf("lat" to "43.65", "lon" to "-79.38"))

        val result = req.queryParams<TestQuery>(json)

        result.lat shouldBe 43.65
        result.lon shouldBe -79.38
    }

    @Test
    fun parsesOptionalFieldWhenPresent() {
        val req = request(mapOf("lat" to "43.65", "lon" to "-79.38", "name" to "Toronto"))

        val result = req.queryParams<TestQuery>(json)

        result.name shouldBe "Toronto"
    }

    @Test
    fun omitsOptionalFieldWhenMissing() {
        val req = request(mapOf("lat" to "43.65", "lon" to "-79.38"))

        val result = req.queryParams<TestQuery>(json)

        result.name shouldBe null
    }

    @Test
    fun throwsBadRequestWhenRequiredFieldMissing() {
        val req = request(mapOf("lat" to "43.65"))

        assertFailsWith<BadRequestException> {
            req.queryParams<TestQuery>(json)
        }
    }

    @Test
    fun parsesSerialNameFields() {
        val req = request(mapOf("max_temp" to "30"))

        val result = req.queryParams<QueryWithSerialName>(json)

        result.maxTemp shouldBe 30
    }
}
