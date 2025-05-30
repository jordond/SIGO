package app.sigot.core.api.server

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public fun apiServerModule(): Module =
    module {
        single {
            DefaultApiRouter(getAll(), get())
        } bind ApiRouter::class
    }
