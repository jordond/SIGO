package now.shouldigooutside.core.api.server.cors

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import now.shouldigooutside.core.api.server.http.ServerRequest
import now.shouldigooutside.core.api.server.http.ServerResponse
import kotlin.test.Test

class DefaultCorsHandlerTest {
    private val handler = DefaultCorsHandler()

    private fun request(origin: String? = null): ServerRequest =
        ServerRequest(
            url = "https://api.example.com/forecast",
            method = HttpMethod.Get,
            headers = if (origin != null) {
                headersOf(HttpHeaders.Origin, origin)
            } else {
                headersOf()
            },
        )

    @Test
    fun validateOriginReturnsNullWhenNoOriginHeader() {
        val result = handler.validateOrigin(request(origin = null))

        result shouldBe null
    }

    @Test
    fun validateOriginReturnsNullForAllowedOrigin() {
        val result = handler.validateOrigin(request(origin = "https://shouldigooutside.now"))

        result shouldBe null
    }

    @Test
    fun validateOriginReturnsForbiddenForDisallowedOrigin() {
        val result = handler.validateOrigin(request(origin = "https://evil.com"))

        result shouldNotBe null
        result!!.statusCode shouldBe 403
    }

    @Test
    fun validateOriginAllowsLocalhostOrigins() {
        handler.validateOrigin(request(origin = "http://localhost:4321")) shouldBe null
        handler.validateOrigin(request(origin = "http://localhost:3000")) shouldBe null
    }

    @Test
    fun validateOriginAllowsStagingOrigin() {
        val result = handler.validateOrigin(request(origin = "https://staging.shouldigooutside.now"))

        result shouldBe null
    }

    @Test
    fun preflightReturns204ForAllowedOrigin() {
        val result = handler.preflight(request(origin = "https://shouldigooutside.now"))

        result.statusCode shouldBe 204
        result.headers[HttpHeaders.AccessControlAllowOrigin] shouldBe "https://shouldigooutside.now"
        result.headers[HttpHeaders.AccessControlMaxAge] shouldBe "86400"
    }

    @Test
    fun preflightReturnsForbiddenForDisallowedOrigin() {
        val result = handler.preflight(request(origin = "https://evil.com"))

        result.statusCode shouldBe 403
    }

    @Test
    fun preflightWithNoOriginReturns204WithoutAllowOriginHeader() {
        val result = handler.preflight(request(origin = null))

        result.statusCode shouldBe 204
        result.headers[HttpHeaders.AccessControlAllowOrigin] shouldBe null
    }

    @Test
    fun withCorsHeadersAddsHeadersForAllowedOrigin() {
        val response = ServerResponse(statusCode = 200, body = "{}")
        val req = request(origin = "https://shouldigooutside.now")

        val result = handler.withCorsHeaders(response, req)

        result.statusCode shouldBe 200
        result.headers[HttpHeaders.AccessControlAllowOrigin] shouldBe "https://shouldigooutside.now"
        result.headers[HttpHeaders.Vary] shouldBe HttpHeaders.Origin
    }

    @Test
    fun withCorsHeadersSkipsForDisallowedOrigin() {
        val response = ServerResponse(statusCode = 200, body = "{}")
        val req = request(origin = "https://evil.com")

        val result = handler.withCorsHeaders(response, req)

        result.headers[HttpHeaders.AccessControlAllowOrigin] shouldBe null
    }

    @Test
    fun withCorsHeadersSkipsWhenNoOrigin() {
        val response = ServerResponse(statusCode = 200, body = "{}")
        val req = request(origin = null)

        val result = handler.withCorsHeaders(response, req)

        result.headers[HttpHeaders.AccessControlAllowOrigin] shouldBe null
    }

    @Test
    fun withCorsHeadersPreservesOriginalHeaders() {
        val response = ServerResponse(
            statusCode = 200,
            headers = headersOf("X-Custom", "value"),
            body = "{}",
        )
        val req = request(origin = "https://shouldigooutside.now")

        val result = handler.withCorsHeaders(response, req)

        result.headers["X-Custom"] shouldBe "value"
        result.headers[HttpHeaders.AccessControlAllowOrigin] shouldBe "https://shouldigooutside.now"
    }

    @Test
    fun customAllowedOriginsAreRespected() {
        val customHandler = DefaultCorsHandler(allowedOrigins = setOf("https://custom.com"))

        customHandler.validateOrigin(request(origin = "https://custom.com")) shouldBe null
        customHandler.validateOrigin(request(origin = "https://shouldigooutside.now"))?.statusCode shouldBe
            403
    }
}
