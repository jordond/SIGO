package now.shouldigooutside.core.api.client.internal

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.client.ApiUrlProvider
import now.shouldigooutside.core.api.model.ApiRoutePath
import now.shouldigooutside.core.api.model.entity.ApiResponse
import now.shouldigooutside.core.api.model.entity.VersionEntity
import now.shouldigooutside.core.api.model.entity.VersionResponse
import now.shouldigooutside.core.api.model.http.ApiHeaders
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.forecast.data.entity.toEntity
import now.shouldigooutside.test.testForecast
import kotlin.test.Test

private val testJson = Json {
    prettyPrint = false
    isLenient = true
    ignoreUnknownKeys = true
}

private class FakeUrlProvider(
    private val baseUrl: String = "https://api.test.com",
) : ApiUrlProvider {
    override fun provide(): String = baseUrl
}

class DefaultApiClientTest {
    @Test
    fun versionParsesResponseCorrectly() =
        runTest {
            val versionResponse = ApiResponse(
                data = VersionResponse(
                    version = VersionEntity(name = "1.0.0", code = 1, sha = "abc123"),
                ),
            )
            val responseJson = testJson.encodeToString(versionResponse)

            val engine = MockEngine { _ ->
                respond(
                    content = responseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString(),
                    ),
                )
            }
            val client = createClient(engine)

            val result = client.version()

            result.data.name shouldBe "1.0.0"
            result.data.code shouldBe 1
            result.data.sha shouldBe "abc123"
        }

    @Test
    fun versionParsesRateLimitHeaders() =
        runTest {
            val versionResponse = ApiResponse(
                data = VersionResponse(
                    version = VersionEntity(name = "1.0.0", code = 1, sha = null),
                ),
            )
            val responseJson = testJson.encodeToString(versionResponse)

            val engine = MockEngine { _ ->
                respond(
                    content = responseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString()),
                        ApiHeaders.RATE_LIMIT to listOf("100"),
                        ApiHeaders.RATE_LIMIT_REMAINING to listOf("95"),
                        ApiHeaders.RATE_LIMIT_RESET to listOf("1700000000"),
                    ),
                )
            }
            val client = createClient(engine)

            val result = client.version()

            val rateLimit = result.rateLimit
            rateLimit shouldNotBe null
            rateLimit!!.limit shouldBe 100
            rateLimit.remaining shouldBe 95
        }

    @Test
    fun versionReturnsNullRateLimitWhenHeadersMissing() =
        runTest {
            val versionResponse = ApiResponse(
                data = VersionResponse(
                    version = VersionEntity(name = "1.0.0", code = 1, sha = null),
                ),
            )
            val responseJson = testJson.encodeToString(versionResponse)

            val engine = MockEngine { _ ->
                respond(
                    content = responseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString(),
                    ),
                )
            }
            val client = createClient(engine)

            val result = client.version()

            result.rateLimit shouldBe null
        }

    @Test
    fun forecastSendsCorrectQueryParams() =
        runTest {
            val forecast = testForecast()
            val forecastResponse = ApiResponse(
                data = now.shouldigooutside.forecast.data.entity.ForecastResponse(
                    forecast = forecast.toEntity(),
                ),
            )
            val responseJson = testJson.encodeToString(forecastResponse)

            var capturedUrl: String? = null
            val engine = MockEngine { request ->
                capturedUrl = request.url.toString()
                respond(
                    content = responseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString(),
                    ),
                )
            }
            val client = createClient(engine)
            val location = Location(latitude = 43.65, longitude = -79.38)

            client.forecast(location)

            val url = capturedUrl
            url shouldNotBe null
            url!!.contains("lat=43.65") shouldBe true
            url.contains("lon=-79.38") shouldBe true
        }

    @Test
    fun forecastParsesResponseCorrectly() =
        runTest {
            val forecast = testForecast()
            val forecastResponse = ApiResponse(
                data = now.shouldigooutside.forecast.data.entity.ForecastResponse(
                    forecast = forecast.toEntity(),
                ),
            )
            val responseJson = testJson.encodeToString(forecastResponse)

            val engine = MockEngine { _ ->
                respond(
                    content = responseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString(),
                    ),
                )
            }
            val client = createClient(engine)

            val result = client.forecast(Location(latitude = 43.65, longitude = -79.38))

            result.data.location.latitude shouldBe forecast.location.latitude
            result.data.location.longitude shouldBe forecast.location.longitude
        }

    @Test
    fun forecastCallsCorrectEndpoint() =
        runTest {
            val forecast = testForecast()
            val forecastResponse = ApiResponse(
                data = now.shouldigooutside.forecast.data.entity.ForecastResponse(
                    forecast = forecast.toEntity(),
                ),
            )
            val responseJson = testJson.encodeToString(forecastResponse)

            var capturedUrl: String? = null
            val engine = MockEngine { request ->
                capturedUrl = request.url.encodedPath
                respond(
                    content = responseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString(),
                    ),
                )
            }
            val client = createClient(engine)

            client.forecast(Location(latitude = 0.0, longitude = 0.0))

            capturedUrl shouldBe "/forecast"
        }

    private fun createClient(engine: MockEngine): DefaultApiClient {
        val httpClient = HttpClient(engine) {
            install(ContentNegotiation) {
                json(testJson)
            }
        }
        return DefaultApiClient(
            urlProvider = FakeUrlProvider(),
            httpClient = httpClient,
            json = testJson,
        )
    }
}
