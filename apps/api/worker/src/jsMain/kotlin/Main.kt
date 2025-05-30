import app.sigot.App
import app.sigot.di.initKoin
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.js.Promise

private val app = initKoin().get<App>()

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetch(
    request: Request,
    env: dynamic,
): Promise<Response> = app.handle(request, env)
