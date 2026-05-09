import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import now.shouldigooutside.api.App
import now.shouldigooutside.api.DefaultApp
import now.shouldigooutside.api.di.requestModule
import now.shouldigooutside.api.provider.ApiVersionProvider
import now.shouldigooutside.core.api.server.jsApiServerModule
import now.shouldigooutside.core.domain.VersionProvider
import now.shouldigooutside.core.foundation.di.foundationModule
import now.shouldigooutside.core.platform.di.jsonModule
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.js.Promise

private val koin: Koin = initKoin()
private val app: App = DefaultApp(koin)

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetch(
    request: Request,
    env: dynamic,
    ctx: dynamic,
): Promise<Response> = app.handle(request, env, ctx)

internal fun initKoin(): Koin =
    startKoin {
        logger(KermitKoinLogger(Logger.withTag("Koin")))

        modules(
            workerModule(),
            jsonModule(),
            jsApiServerModule(),
            foundationModule(),
            // NOTE: forecastBackendModule() is intentionally omitted — every type it
            // provides is declared per-request inside requestModule()'s RequestScope.
            // Including it would leave dead root-level factories that future readers
            // must reason about; worse, those root factories cannot resolve the
            // request-scoped HttpClient / ApiTokenProvider and would fail at runtime
            // if anything ever invoked them at the root level.
            // NOTE: networkModule() is intentionally omitted — HttpClient is declared
            // inside requestModule()'s RequestScope instead.
            requestModule(),
        )
    }.koin

private fun workerModule() =
    module {
        singleOf(::ApiVersionProvider) bind VersionProvider::class
    }
