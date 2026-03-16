package now.shouldigooutside.core.api.server

import co.touchlab.kermit.Logger
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.model.http.ApiHeaders
import now.shouldigooutside.core.api.server.cache.CacheProvider
import now.shouldigooutside.core.api.server.cors.CorsHandler
import now.shouldigooutside.core.api.server.exception.BadRequestException
import now.shouldigooutside.core.api.server.http.ServerRequest
import now.shouldigooutside.core.api.server.http.ServerResponse
import now.shouldigooutside.core.api.server.ratelimit.RateLimiter
import now.shouldigooutside.core.api.server.util.badRequest
import now.shouldigooutside.core.api.server.util.methodNotAllowed
import now.shouldigooutside.core.api.server.util.noContent
import now.shouldigooutside.core.api.server.util.notFound
import now.shouldigooutside.core.api.server.util.serverError
import now.shouldigooutside.core.api.server.util.tooManyRequests
import now.shouldigooutside.core.api.server.util.unauthorized
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

        val clientId = Uuid.parseOrNull(request.headers[ApiHeaders.CLIENT_ID] ?: "")
        if (clientId == null) {
            return corsHandler.withCorsHeaders(
                response = unauthorized(
                    meta = mapOf("error" to "Missing or invalid ${ApiHeaders.CLIENT_ID} header"),
                    json = json,
                ),
                request = request,
            )
        }

        val ipAddress = request.headers[ApiHeaders.CONNECTING_IP]
        val cache = cacheProvider.cache
        val rateLimitResult = if (cache != null) {
            rateLimiter.check(clientId, ipAddress, cache)
        } else {
            null
        }

        if (rateLimitResult != null && !rateLimitResult.allowed) {
            val finalResponse = addRateLimitHeaders(
                response = tooManyRequests(
                    meta = mapOf("error" to "Rate limit exceeded"),
                    json = json,
                ),
                result = rateLimitResult,
            )
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
        val headers = HeadersBuilder()
            .apply {
                appendAll(response.headers)
                append(ApiHeaders.RATE_LIMIT, result.limit.toString())
                append(ApiHeaders.RATE_LIMIT_REMAINING, result.remaining.toString())
                append(ApiHeaders.RATE_LIMIT_RESET, result.resetAt.epochSeconds.toString())
            }.build()
        return response.copy(headers = headers)
    }

    private fun extractPath(url: String): String {
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
    ): ServerResponse = route.get(request, parameters) ?: noContent(json = json)

    private suspend fun post(
        route: ApiRoute,
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse = route.post(request, parameters) ?: noContent(json = json)

    private suspend fun put(
        route: ApiRoute,
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse = route.put(request, parameters) ?: noContent(json = json)

    private suspend fun delete(
        route: ApiRoute,
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse = route.delete(request, parameters) ?: noContent(json = json)
}
