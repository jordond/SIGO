package app.sigot.api

import app.sigot.api.di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.js.Promise

private val koin = initKoin().koin
private val scope = koin.get<CoroutineScope>()

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetch(request: Request): Promise<Response> =
    scope.promise {
        val headers: dynamic = object {}
        headers["content-type"] = "text/plain"

        val injected = koin.get<String>()
        Response(
            "Kotlin Worker: ${request.url}, injected: $injected",
            ResponseInit(headers = headers),
        )
    }
