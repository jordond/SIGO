import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import now.shouldigooutside.api.App
import now.shouldigooutside.api.DefaultApp
import now.shouldigooutside.api.provider.ApiVersionProvider
import now.shouldigooutside.api.provider.WorkerTokenProvider
import now.shouldigooutside.core.api.server.jsApiServerModule
import now.shouldigooutside.core.domain.VersionProvider
import now.shouldigooutside.core.domain.forecast.ApiTokenProvider
import now.shouldigooutside.core.foundation.di.foundationModule
import now.shouldigooutside.core.platform.di.networkModule
import now.shouldigooutside.forecast.forecastBackendModule
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
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

internal fun initKoin(): Koin =
    startKoin {
        logger(KermitKoinLogger(Logger.withTag("Koin")))

        modules(
            workerModule(),
            jsApiServerModule(),
            foundationModule(),
            forecastBackendModule(),
            networkModule(),
        )
    }.koin

private fun workerModule() =
    module {
        singleOf(::WorkerTokenProvider) bind ApiTokenProvider::class
        singleOf(::ApiVersionProvider) bind VersionProvider::class
        singleOf(::DefaultApp) bind App::class
    }
