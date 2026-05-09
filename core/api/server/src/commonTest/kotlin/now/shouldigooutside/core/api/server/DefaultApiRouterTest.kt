package now.shouldigooutside.core.api.server

import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.model.ApiRoutePath
import now.shouldigooutside.core.api.model.http.ApiHeaders
import now.shouldigooutside.core.api.server.cors.CorsHandler
import now.shouldigooutside.core.api.server.exception.BadRequestException
import now.shouldigooutside.core.api.server.http.ServerRequest
import now.shouldigooutside.core.api.server.http.ServerResponse
import kotlin.test.Test
import kotlin.uuid.Uuid

private val testClientId = Uuid.random().toString()

private fun request(
    url: String = "https://api.example.com/forecast",
    method: HttpMethod = HttpMethod.Get,
    origin: String = "https://shouldigooutside.now",
    clientId: String? = testClientId,
): ServerRequest =
    ServerRequest(
        url = url,
        method = method,
        headers = headersOf(
            *buildList {
                add("Origin" to listOf(origin))
                if (clientId != null) add(ApiHeaders.CLIENT_ID to listOf(clientId))
            }.toTypedArray(),
        ),
    )

private class PassthroughCorsHandler : CorsHandler {
    override fun validateOrigin(request: ServerRequest): ServerResponse? = null

    override fun preflight(request: ServerRequest): ServerResponse = ServerResponse(statusCode = 204)

    override fun withCorsHeaders(
        response: ServerResponse,
        request: ServerRequest,
    ): ServerResponse = response
}

private class FakeRoute(
    override val path: ApiRoutePath = ApiRoutePath.Forecast,
    private val getResponse: ServerResponse? = ServerResponse(statusCode = 200, body = "ok"),
) : ApiRoute {
    var lastRequest: ServerRequest? = null
    var lastParameters: Map<String, String>? = null

    override suspend fun get(
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse? {
        lastRequest = request
        lastParameters = parameters
        return getResponse
    }
}

private class ThrowingRoute(
    override val path: ApiRoutePath = ApiRoutePath.Forecast,
    private val exception: Throwable = RuntimeException("boom"),
) : ApiRoute {
    override suspend fun get(
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse? = throw exception
}

private fun router(
    routes: List<ApiRoute> = emptyList(),
    corsHandler: CorsHandler = PassthroughCorsHandler(),
): DefaultApiRouter =
    DefaultApiRouter(
        routes = routes,
        json = Json { ignoreUnknownKeys = true },
        corsHandler = corsHandler,
    )

class DefaultApiRouterTest {
    @Test
    fun missingClientIdReturns401() =
        runTest {
            val router = router()

            val result = router.handle(request(clientId = null))

            result.statusCode shouldBe 401
        }

    @Test
    fun invalidClientIdReturns401() =
        runTest {
            val router = router()

            val result = router.handle(request(clientId = "not-a-uuid"))

            result.statusCode shouldBe 401
        }

    @Test
    fun optionsRequestReturnsPreflight() =
        runTest {
            val router = router()

            val result = router.handle(request(method = HttpMethod.Options))

            result.statusCode shouldBe 204
        }

    @Test
    fun unknownPathReturns404() =
        runTest {
            val router = router()

            val result = router.handle(request(url = "https://api.example.com/unknown"))

            result.statusCode shouldBe 404
        }

    @Test
    fun matchingRouteReturnsRouteResponse() =
        runTest {
            val route = FakeRoute()
            val router = router(routes = listOf(route))

            val result = router.handle(request(url = "https://api.example.com/forecast"))

            result.statusCode shouldBe 200
            result.body shouldBe "ok"
        }

    @Test
    fun routeExceptionReturns500() =
        runTest {
            val route = ThrowingRoute()
            val router = router(routes = listOf(route))

            val result = router.handle(request(url = "https://api.example.com/forecast"))

            result.statusCode shouldBe 500
        }

    @Test
    fun badRequestExceptionReturns400() =
        runTest {
            val route = ThrowingRoute(exception = BadRequestException(validation = listOf("lat is required")))
            val router = router(routes = listOf(route))

            val result = router.handle(request(url = "https://api.example.com/forecast"))

            result.statusCode shouldBe 400
        }

    @Test
    fun notImplementedReturns405() =
        runTest {
            val route = FakeRoute()
            val router = router(routes = listOf(route))

            val result = router.handle(
                request(url = "https://api.example.com/forecast", method = HttpMethod.Post),
            )

            result.statusCode shouldBe 405
        }

    @Test
    fun pathExtractionHandlesQueryStrings() =
        runTest {
            val route = FakeRoute()
            val router = router(routes = listOf(route))

            val result = router.handle(
                request(url = "https://api.example.com/forecast?lat=43&lon=-79"),
            )

            result.statusCode shouldBe 200
        }

    @Test
    fun pathMatchingWithTemplateParameters() =
        runTest {
            val route = FakeRoute(path = ApiRoutePath.Forecast)
            val router = router(routes = listOf(route))

            val result = router.handle(request(url = "https://api.example.com/forecast"))

            result.statusCode shouldBe 200
        }

    @Test
    fun corsBlockedOriginReturnsEarly() =
        runTest {
            val blockingCors = object : CorsHandler {
                override fun validateOrigin(request: ServerRequest): ServerResponse =
                    ServerResponse(statusCode = 403, body = "blocked")

                override fun preflight(request: ServerRequest): ServerResponse =
                    ServerResponse(statusCode = 204)

                override fun withCorsHeaders(
                    response: ServerResponse,
                    request: ServerRequest,
                ): ServerResponse = response
            }
            val router = router(corsHandler = blockingCors)

            val result = router.handle(request())

            result.statusCode shouldBe 403
        }

    @Test
    fun unsupportedMethodReturns405() =
        runTest {
            val route = FakeRoute()
            val router = router(routes = listOf(route))

            val result = router.handle(
                request(url = "https://api.example.com/forecast", method = HttpMethod.Patch),
            )

            result.statusCode shouldBe 405
        }
}
