package app.sigot.api.routes

import app.sigot.core.Version
import app.sigot.core.api.server.ApiRoute
import app.sigot.core.platform.http.ok
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.fetch.Request
import org.w3c.fetch.Response

@Serializable
data class RootResponse(
    @SerialName("version")
    val version: String,
    @SerialName("sha")
    val sha: String,
)

class RootRoute : ApiRoute {
    override val path: String = "/"

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response {
        val response = RootResponse(
            version = Version.NAME,
            sha = Version.GIT_SHA,
        )
        return ok(response)
    }
}
