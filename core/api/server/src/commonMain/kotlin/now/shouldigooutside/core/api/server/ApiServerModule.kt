package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.cors.CorsHandler
import now.shouldigooutside.core.api.server.cors.DefaultCorsHandler
import org.koin.core.module.Module
import org.koin.dsl.module

public fun commonApiServerModule(): Module =
    module {
        single<CorsHandler> { DefaultCorsHandler() }
    }
