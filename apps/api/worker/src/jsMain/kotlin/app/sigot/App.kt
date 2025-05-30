package app.sigot

import app.sigot.core.api.server.ApiRouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.js.Promise

interface App {
    fun handle(request: Request): Promise<Response>
}

class DefaultApp(
    scope: CoroutineScope,
    val router: ApiRouter,
) : App,
    CoroutineScope by scope {
    override fun handle(request: Request): Promise<Response> = promise { router.handle(request) }
}
