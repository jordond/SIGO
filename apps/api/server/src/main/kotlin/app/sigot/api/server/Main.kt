package app.sigot.api.server

import app.sigot.api.server.provider.EnvTokenProvider
import app.sigot.api.server.provider.JvmVersionProvider
import app.sigot.core.api.server.ApiRouter
import app.sigot.core.api.server.http.mountApiRouter
import app.sigot.core.api.server.jvmApiServerModule
import app.sigot.core.domain.VersionProvider
import app.sigot.core.domain.forecast.ApiTokenProvider
import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.di.networkModule
import app.sigot.forecast.forecastBackendModule
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    val koin = startKoin {
        logger(KermitKoinLogger(Logger.withTag("Koin")))
        modules(
            serverModule(),
            jvmApiServerModule(),
            foundationModule(),
            forecastBackendModule(),
            networkModule(),
        )
    }.koin

    val router = koin.get<ApiRouter>()

    embeddedServer(Netty, port = port) {
        routing {
            mountApiRouter(router)
        }
    }.start(wait = true)
}

private fun serverModule() =
    module {
        single<ApiTokenProvider> { EnvTokenProvider() } bind ApiTokenProvider::class
        single<VersionProvider> { JvmVersionProvider() } bind VersionProvider::class
    }
