package app.sigot.core.api.server.routes

import app.sigot.core.api.model.ApiRoutePath
import app.sigot.core.api.model.entity.VersionResponse
import app.sigot.core.api.model.entity.toEntity
import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse
import app.sigot.core.api.server.util.cached
import app.sigot.core.api.server.util.ok
import app.sigot.core.domain.VersionProvider
import kotlin.time.Duration.Companion.hours

public class VersionRoute(
    private val versionProvider: VersionProvider,
) : ApiRoute {
    override val path: ApiRoutePath = ApiRoutePath.Version

    override suspend fun get(
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        val version = versionProvider.provide().toEntity()
        val response = VersionResponse(version)
        return cached(24.hours) {
            ok(response)
        }
    }
}
