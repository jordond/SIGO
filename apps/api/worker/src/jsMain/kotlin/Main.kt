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
): Promise<Response> = app.handle(request, env)

internal fun initKoin(): Koin =
    startKoin {
        logger(KermitKoinLogger(Logger.withTag("Koin")))

        modules(
            workerModule(),
            jsonModule(),
            jsApiServerModule(),
            foundationModule(),
            // NOTE: forecastBackendModule() is intentionally omitted — every binding it
            // declares is shadowed by `scoped<...>` declarations in requestModule(), and
            // those are the only consumers on the worker. Keeping it would leave dead
            // root-level factories that future readers must reason about.
            // NOTE: networkModule() is intentionally omitted — HttpClient is declared
            // inside requestModule()'s RequestScope instead.
            requestModule(),
        )
    }.koin

private fun workerModule() =
    module {
        singleOf(::ApiVersionProvider) bind VersionProvider::class
    }
