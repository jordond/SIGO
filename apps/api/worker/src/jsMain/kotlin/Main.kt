package app.sigot.api

import app.sigot.api.di.koin
import app.sigot.build.BuildKonfig.API_VERSION
import app.sigot.core.platform.http.respondJson
import app.sigot.core.platform.http.respondText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import org.w3c.dom.url.URL
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.js.Promise

private val scope = koin.get<CoroutineScope>()

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetch(request: Request): Promise<Response> =
    scope.promise {
        val url = URL(request.url)
        val path = url.pathname
        val searchParams = url.searchParams

        if (path == "/") {
            val data = mapOf("version" to API_VERSION)
            val res = runCatching {  respondJson(data) }.recover { respondText("Error: ${it.message}", status = 500) }
            return@promise res.getOrThrow()
        }

        // Get all query parameters
        val params = mutableMapOf<String, String>()
        searchParams.asDynamic().forEach { value: String, key: String ->
            params[key] = value
        }

        val headers: dynamic = object {}
        headers["content-type"] = "text/plain"

        Response(
            "Kotlin Worker: ${request.url}\nPath: $path\nQuery params: $params",
            ResponseInit(headers = headers),
        )
    }
