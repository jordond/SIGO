package app.sigot.core.api.server

import app.sigot.core.api.server.cache.ForecastCacheProvider
import app.sigot.core.api.server.cors.CorsHandler
import app.sigot.core.api.server.exception.BadRequestException
import app.sigot.core.api.server.ratelimit.RateLimiter
import app.sigot.core.api.server.util.badRequest
import app.sigot.core.api.server.util.methodNotAllowed
import app.sigot.core.api.server.util.noContent
import app.sigot.core.api.server.util.notFound
import app.sigot.core.api.server.util.serverError
import app.sigot.core.api.server.util.tooManyRequests
import app.sigot.core.api.server.util.unauthorized
import co.touchlab.kermit.Logger
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import org.w3c.dom.url.URL
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit

/**
 * Router interface for managing multiple API routes
 */
public interface ApiRouter {
    public suspend fun handle(request: Request): Response
}

internal class DefaultApiRouter(
    private val routes: List<ApiRoute>,
    private val json: Json,
    private val cacheProvider: ForecastCacheProvider,
    private val rateLimiter: RateLimiter,
) : ApiRouter {
    private val logger = Logger.withTag("ApiRouter")

    private val uuidRegex = Regex(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
        RegexOption.IGNORE_CASE,
    )

    private data class RouteMatch(
        val route: ApiRoute,
        val parameters: Map<String, String>,
    )

    override suspend fun handle(request: Request): Response {
        val method = request.method.uppercase()

        val corsBlock = CorsHandler.validateOrigin(request)
        if (corsBlock != null) return corsBlock

        if (method == "OPTIONS") {
            return CorsHandler.preflight(request)
        }

        val clientId = request.headers.get("X-Client-ID")
        if (clientId == null || !uuidRegex.matches(clientId)) {
            return CorsHandler.withCorsHeaders(
                unauthorized(
                    meta = mapOf("error" to "Missing or invalid X-Client-ID header"),
                    json = json,
                ),
                request,
            )
        }

        val ipAddress = request.headers.get("CF-Connecting-IP")

        val cache = cacheProvider.cache
        val rateLimitResult = if (cache != null) {
            rateLimiter.check(clientId, ipAddress, cache)
        } else {
            null
        }

        if (rateLimitResult != null && !rateLimitResult.allowed) {
            var finalResponse = tooManyRequests(
                meta = mapOf("error" to "Rate limit exceeded"),
                json = json,
            )
            finalResponse = addRateLimitHeaders(finalResponse, rateLimitResult)
            return CorsHandler.withCorsHeaders(finalResponse, request)
        }

        val url = URL(request.url)
        val path = url.pathname

        val match = findMatchingRoute(path)
            ?: return CorsHandler.withCorsHeaders(
                notFound(meta = mapOf("path" to path)),
                request,
            )

        val response = try {
            when (method) {
                HttpMethod.Get.value -> get(match.route, request, match.parameters)
                HttpMethod.Post.value -> post(match.route, request, match.parameters)
                HttpMethod.Put.value -> put(match.route, request, match.parameters)
                HttpMethod.Delete.value -> delete(match.route, request, match.parameters)
                else -> methodNotAllowed()
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: NotImplementedError) {
            methodNotAllowed(meta = mapOf("path" to path, "method" to method), json = json)
        } catch (badRequest: BadRequestException) {
            val validation = badRequest.validation.joinToString(",")
            badRequest(
                meta = mapOf("path" to path, "validation" to validation),
                json = json,
            )
        } catch (cause: Throwable) {
            logger.e(cause) { "Error handling request for path: $path" }
            serverError(cause, meta = mapOf("path" to path), json = json)
        }

        val finalResponse = if (rateLimitResult != null) {
            addRateLimitHeaders(response, rateLimitResult)
        } else {
            response
        }

        return CorsHandler.withCorsHeaders(finalResponse, request)
    }

    private fun addRateLimitHeaders(
        response: Response,
        result: RateLimiter.RateLimitResult,
    ): Response {
        val headers: dynamic = object {}
        response.headers.asDynamic().forEach { value: String, key: String ->
            headers[key] = value
        }
        headers["X-RateLimit-Limit"] = result.limit.toString()
        headers["X-RateLimit-Remaining"] = result.remaining.toString()
        headers["X-RateLimit-Reset"] = result.resetEpochSeconds.toString()

        return Response(
            response.body,
            ResponseInit(
                status = response.status,
                statusText = response.statusText,
                headers = headers,
            ),
        )
    }

    private fun findMatchingRoute(requestPath: String): RouteMatch? {
        for (route in routes) {
            val parameters = matchRoute(route.path.path, requestPath)
            if (parameters != null) {
                return RouteMatch(route, parameters)
            }
        }
        return null
    }

    private fun matchRoute(
        routePattern: String,
        requestPath: String,
    ): Map<String, String>? {
        val routeSegments = routePattern.split("/").filter { it.isNotEmpty() }
        val pathSegments = requestPath.split("/").filter { it.isNotEmpty() }

        if (routeSegments.size != pathSegments.size) {
            return null
        }

        val parameters = mutableMapOf<String, String>()

        for (i in routeSegments.indices) {
            val routeSegment = routeSegments[i]
            val pathSegment = pathSegments[i]

            when {
                routeSegment.startsWith("{") && routeSegment.endsWith("}") -> {
                    // This is a parameter
                    val paramName = routeSegment.substring(1, routeSegment.length - 1)
                    parameters[paramName] = pathSegment
                }
                routeSegment == pathSegment -> {
                    // Exact match for static segment
                    continue
                }
                else -> {
                    // No match
                    return null
                }
            }
        }

        return parameters
    }

    private suspend fun get(
        route: ApiRoute,
        request: Request,
        parameters: Map<String, String>,
    ): Response {
        val result = route.get(request, parameters)
        return result ?: noContent(json = json)
    }

    private suspend fun post(
        route: ApiRoute,
        request: Request,
        parameters: Map<String, String>,
    ): Response {
        val result = route.post(request, parameters)
        return result ?: noContent(json = json)
    }

    private suspend fun put(
        route: ApiRoute,
        request: Request,
        parameters: Map<String, String>,
    ): Response {
        val result = route.put(request, parameters)
        return result ?: noContent(json = json)
    }

    private suspend fun delete(
        route: ApiRoute,
        request: Request,
        parameters: Map<String, String>,
    ): Response {
        val result = route.delete(request, parameters)
        return result ?: noContent(json = json)
    }
}
