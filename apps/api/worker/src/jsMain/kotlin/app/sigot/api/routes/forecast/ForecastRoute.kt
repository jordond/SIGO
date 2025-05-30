package app.sigot.api.routes.forecast

import app.sigot.api.ApiRoute
import app.sigot.api.queryParams
import app.sigot.core.platform.http.ok
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.fetch.Request
import org.w3c.fetch.Response

@Serializable
private data class Query(
    val lat: Double,
    val lon: Double,
    val name: String? = null,
)

class ForecastRoute(
    private val json: Json,
) : ApiRoute {
    override val path: String = "/forecast"

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response? {
        val query = request.queryParams<Query>(json = json)
        return ok(query)
    }
}
