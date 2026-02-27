import app.sigot.api.App
import app.sigot.api.DefaultApp
import app.sigot.api.provider.ApiVersionProvider
import app.sigot.api.provider.WorkerTokenProvider
import app.sigot.core.api.server.jsApiServerModule
import app.sigot.core.domain.VersionProvider
import app.sigot.core.domain.forecast.ApiTokenProvider
import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.di.platformModule
import app.sigot.forecast.forecastBackendModule
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
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
            platformModule(),
        )
    }.koin

private fun workerModule() =
    module {
        singleOf(::WorkerTokenProvider) bind ApiTokenProvider::class
        singleOf(::ApiVersionProvider) bind VersionProvider::class
        singleOf(::DefaultApp) bind App::class
    }
