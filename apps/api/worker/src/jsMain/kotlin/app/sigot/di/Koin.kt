package app.sigot.di

import app.sigot.App
import app.sigot.DefaultApp
import app.sigot.WorkerTokenProvider
import app.sigot.api.routes.RootRoute
import app.sigot.api.routes.forecast.ForecastRoute
import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.apiServerModule
import app.sigot.core.domain.forecast.ApiTokenProvider
import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.di.platformModule
import app.sigot.forecast.forecastBackendModule
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun initKoin(): Koin =
    startKoin {
        logger(KermitKoinLogger(Logger.withTag("Koin")))

        modules(
            workerModule(),
            apiServerModule(),
            foundationModule(),
            forecastBackendModule(),
            platformModule(),
        )
    }.koin

private fun workerModule() =
    module {
        factoryOf(::RootRoute) bind ApiRoute::class
        factoryOf(::ForecastRoute) bind ApiRoute::class

        singleOf(::WorkerTokenProvider) bind ApiTokenProvider::class
        singleOf(::DefaultApp) bind App::class
    }
