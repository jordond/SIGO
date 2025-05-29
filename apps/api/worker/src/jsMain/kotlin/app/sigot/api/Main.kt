package app.sigot.api

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.js.Promise

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetch(request: Request): Promise<Response> =
    GlobalScope.promise {
        val headers: dynamic = object {}
        headers["content-type"] = "text/plain"

        // Get the request body using request.json().await()
        val body = request.json().await()
        Response(
            "Kotlin Worker hello world: ${request.url}, body?: $body",
            ResponseInit(headers = headers),
        )
    }
