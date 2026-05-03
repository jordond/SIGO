package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.cors.CorsHandler
import now.shouldigooutside.core.api.server.cors.DefaultCorsHandler
import now.shouldigooutside.core.api.server.ratelimit.DefaultRateLimiter
import now.shouldigooutside.core.api.server.ratelimit.RateLimiter
import org.koin.core.module.Module
import org.koin.dsl.module

public fun commonApiServerModule(): Module =
    module {
        single<RateLimiter> { DefaultRateLimiter(json = get()) }
        single<CorsHandler> { DefaultCorsHandler() }
    }
