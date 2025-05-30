package app.sigot.api.routes

import app.sigot.api.ApiRoute
import app.sigot.build.BuildKonfig
import app.sigot.core.platform.http.ok
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.fetch.Request
import org.w3c.fetch.Response

@Serializable
data class RootResponse(
    @SerialName("version")
    val version: String,
)

class RootRoute : ApiRoute {
    override val path: String = "/"

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response = ok(RootResponse(version = BuildKonfig.API_VERSION))
}
