package app.sigot.core.api.server

import app.sigot.core.api.server.exception.BadRequestException
import app.sigot.core.api.server.util.badRequest
import app.sigot.core.api.server.util.methodNotAllowed
import app.sigot.core.api.server.util.noContent
import app.sigot.core.api.server.util.notFound
import app.sigot.core.api.server.util.serverError
import co.touchlab.kermit.Logger
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import org.w3c.dom.url.URL
import org.w3c.fetch.Request
import org.w3c.fetch.Response

/**
 * Router interface for managing multiple API routes
 */
public interface ApiRouter {
    public suspend fun handle(request: Request): Response
}

internal class DefaultApiRouter(
    private val routes: List<ApiRoute>,
    private val json: Json,
) : ApiRouter {
    private val logger = Logger.withTag("ApiRouter")

    private data class RouteMatch(
        val route: ApiRoute,
        val parameters: Map<String, String>,
    )

    override suspend fun handle(request: Request): Response {
        val url = URL(request.url)
        val path = url.pathname
        val method = request.method.uppercase()

        val match = findMatchingRoute(path)
            ?: return notFound(meta = mapOf("path" to path))

        return try {
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
