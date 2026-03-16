package now.shouldigooutside.api.server

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import now.shouldigooutside.api.server.provider.EnvTokenProvider
import now.shouldigooutside.api.server.provider.JvmVersionProvider
import now.shouldigooutside.core.api.server.ApiRouter
import now.shouldigooutside.core.api.server.http.mountApiRouter
import now.shouldigooutside.core.api.server.jvmApiServerModule
import now.shouldigooutside.core.domain.VersionProvider
import now.shouldigooutside.core.domain.forecast.ApiTokenProvider
import now.shouldigooutside.core.foundation.di.foundationModule
import now.shouldigooutside.core.platform.di.networkModule
import now.shouldigooutside.forecast.forecastBackendModule
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
