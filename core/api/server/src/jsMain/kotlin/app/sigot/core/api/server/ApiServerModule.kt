package app.sigot.core.api.server

import app.sigot.core.api.server.routes.RootRoute
import app.sigot.core.api.server.routes.forecast.ForecastRoute
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun jsApiServerModule(): Module =
    module {
        factoryOf(::RootRoute) bind ApiRoute::class
        factoryOf(::ForecastRoute) bind ApiRoute::class

        single {
            DefaultApiRouter(getAll(), get())
        } bind ApiRouter::class
    }
