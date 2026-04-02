package now.shouldigooutside.core.api.server.util

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.http.HttpHeaders
import now.shouldigooutside.core.api.server.http.ContentType
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes

class ResponseTest {
    @Test
    fun respondTextReturnsPlainTextResponse() {
        val result = respondText("hello")

        result.statusCode shouldBe 200
        result.body shouldBe "hello"
        result.headers[HttpHeaders.ContentType] shouldBe ContentType.TEXT_PLAIN
    }

    @Test
    fun respondTextCustomStatus() {
        val result = respondText("created", status = 201, statusText = "Created")

        result.statusCode shouldBe 201
        result.statusText shouldBe "Created"
    }

    @Test
    fun respondJsonReturnsJsonResponse() {
        val result = respondJson(json = """{"key":"value"}""")

        result.statusCode shouldBe 200
        result.body shouldBe """{"key":"value"}"""
        result.headers[HttpHeaders.ContentType] shouldBe ContentType.JSON
    }

    @Test
    fun badRequestReturns400() {
        val result = badRequest()

        result.statusCode shouldBe 400
        result.statusText shouldBe "Bad Request"
    }

    @Test
    fun notFoundReturns404() {
        val result = notFound()

        result.statusCode shouldBe 404
    }

    @Test
    fun serverErrorReturns500() {
        val result = serverError()

        result.statusCode shouldBe 500
        result.body shouldNotBe null
    }

    @Test
    fun unauthorizedReturns401() {
        val result = unauthorized()

        result.statusCode shouldBe 401
    }

    @Test
    fun tooManyRequestsReturns429() {
        val result = tooManyRequests()

        result.statusCode shouldBe 429
    }

    @Test
    fun cachedAddsCacheControlHeader() {
        val result = cached(15.minutes) {
            respondJson(json = """{"data":"test"}""")
        }

        result.headers[HttpHeaders.CacheControl] shouldBe "max-age=900"
        result.body shouldBe """{"data":"test"}"""
    }

    @Test
    fun noContentReturns204() {
        val result = noContent()

        result.statusCode shouldBe 204
    }

    @Test
    fun serverErrorContainsMessage() {
        val result = serverError(message = "something broke")

        result.body!! shouldContain "something broke"
    }
}
