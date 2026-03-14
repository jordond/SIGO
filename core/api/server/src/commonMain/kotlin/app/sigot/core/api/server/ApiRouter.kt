package app.sigot.core.api.server

import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse

public interface ApiRouter {
    public suspend fun handle(request: ServerRequest): ServerResponse
}
