package app.sigot.core.api.server

import app.sigot.core.api.server.cache.CacheProvider
import app.sigot.core.api.server.cors.CorsHandler
import app.sigot.core.api.server.exception.BadRequestException
import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse
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
import kotlin.uuid.Uuid

internal class DefaultApiRouter(
    private val routes: List<ApiRoute>,
    private val json: Json,
    private val cacheProvider: CacheProvider,
    private val rateLimiter: RateLimiter,
    private val corsHandler: CorsHandler,
) : ApiRouter {
    private val logger = Logger.withTag("ApiRouter")

    private data class RouteMatch(
        val route: ApiRoute,
        val parameters: Map<String, String>,
    )

    override suspend fun handle(request: ServerRequest): ServerResponse {
        val corsBlock = corsHandler.validateOrigin(request)
        if (corsBlock != null) return corsBlock

        if (request.method == HttpMethod.Options) {
            return corsHandler.preflight(request)
        }

        val clientId = Uuid.parseOrNull(request.headers["X-Client-ID"] ?: "")
        if (clientId == null) {
            return corsHandler.withCorsHeaders(
                unauthorized(
                    meta = mapOf("error" to "Missing or invalid X-Client-ID header"),
                    json = json,
                ),
                request,
            )
        }

        val ipAddress = request.headers["CF-Connecting-IP"]

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
            return corsHandler.withCorsHeaders(finalResponse, request)
        }

        val path = extractPath(request.url)

        val match = findMatchingRoute(path)
            ?: return corsHandler.withCorsHeaders(
                notFound(meta = mapOf("path" to path)),
                request,
            )

        val response = try {
            when (request.method) {
                HttpMethod.Get -> get(match.route, request, match.parameters)
                HttpMethod.Post -> post(match.route, request, match.parameters)
                HttpMethod.Put -> put(match.route, request, match.parameters)
                HttpMethod.Delete -> delete(match.route, request, match.parameters)
                else -> methodNotAllowed(request.method)
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: NotImplementedError) {
            methodNotAllowed(method = request.method, meta = mapOf("path" to path), json = json)
        } catch (badRequest: BadRequestException) {
            val validation = badRequest.validation.joinToString(",")
            badRequest(
                meta = mapOf("path" to path, "validation" to validation),
                json = json,
            )
        } catch (cause: Throwable) {
            logger.e(cause) { "Error handling request for path: $path" }
            serverError(meta = mapOf("path" to path), json = json)
        }

        val finalResponse =
            if (rateLimitResult == null) response else addRateLimitHeaders(response, rateLimitResult)

        return corsHandler.withCorsHeaders(finalResponse, request)
    }

    private fun addRateLimitHeaders(
        response: ServerResponse,
        result: RateLimiter.RateLimitResult,
    ): ServerResponse {
        val headers = response.headers.toMutableMap()
        headers["X-RateLimit-Limit"] = result.limit.toString()
        headers["X-RateLimit-Remaining"] = result.remaining.toString()
        headers["X-RateLimit-Reset"] = result.resetAt.epochSeconds.toString()
        return response.copy(headers = headers)
    }

    private fun extractPath(url: String): String {
        // Extract pathname from URL: strip scheme+host and query string
        val withoutScheme = url.substringAfter("://")
        val pathAndQuery = if (withoutScheme.contains("/")) {
            "/" + withoutScheme.substringAfter("/")
        } else {
            "/"
        }
        return pathAndQuery.substringBefore("?")
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
                    val paramName = routeSegment.substring(1, routeSegment.length - 1)
                    parameters[paramName] = pathSegment
                }
                routeSegment == pathSegment -> {
                    continue
                }
                else -> {
                    return null
                }
            }
        }

        return parameters
    }

    private suspend fun get(
        route: ApiRoute,
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        val result = route.get(request, parameters)
        return result ?: noContent(json = json)
    }

    private suspend fun post(
        route: ApiRoute,
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        val result = route.post(request, parameters)
        return result ?: noContent(json = json)
    }

    private suspend fun put(
        route: ApiRoute,
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        val result = route.put(request, parameters)
        return result ?: noContent(json = json)
    }

    private suspend fun delete(
        route: ApiRoute,
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        val result = route.delete(request, parameters)
        return result ?: noContent(json = json)
    }
}
