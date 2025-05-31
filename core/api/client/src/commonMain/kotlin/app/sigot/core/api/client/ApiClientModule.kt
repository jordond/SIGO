package app.sigot.core.api.client

import app.sigot.core.api.client.internal.DefaultApiClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun apiClientModule(): Module =
    module {
        factoryOf(::DefaultApiClient) bind ApiClient::class
    }
