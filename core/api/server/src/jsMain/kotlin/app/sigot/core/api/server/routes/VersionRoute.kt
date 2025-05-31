package app.sigot.core.api.server.routes

import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.ApiRoutePath
import app.sigot.core.api.server.entity.VersionResponse
import app.sigot.core.api.server.entity.toEntity
import app.sigot.core.api.server.util.cached
import app.sigot.core.api.server.util.ok
import app.sigot.core.domain.VersionProvider
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.time.Duration.Companion.hours

public class VersionRoute(
    private val versionProvider: VersionProvider,
) : ApiRoute {
    override val path: ApiRoutePath = ApiRoutePath.Version

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response {
        val version = versionProvider.provide().toEntity()
        val response = VersionResponse(version)
        return cached(24.hours) {
            ok(response)
        }
    }
}
