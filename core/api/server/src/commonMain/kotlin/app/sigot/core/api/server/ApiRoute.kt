package app.sigot.core.api.server

import app.sigot.core.api.model.ApiRoutePath
import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse

public interface ApiRoute {
    public val path: ApiRoutePath

    public suspend fun get(
        request: ServerRequest,
        parameters: Map<String, String> = emptyMap(),
    ): ServerResponse? = throw NotImplementedError()

    public suspend fun post(
        request: ServerRequest,
        parameters: Map<String, String> = emptyMap(),
    ): ServerResponse? = throw NotImplementedError()

    public suspend fun put(
        request: ServerRequest,
        parameters: Map<String, String> = emptyMap(),
    ): ServerResponse? = throw NotImplementedError()

    public suspend fun delete(
        request: ServerRequest,
        parameters: Map<String, String> = emptyMap(),
    ): ServerResponse? = throw NotImplementedError()
}
