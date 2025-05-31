package app.sigot.core.config

import app.sigot.core.config.firebase.FirebaseAppConfigProvider
import app.sigot.core.model.Initializable
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun configModule(): Module =
    module {
        singleOf(::FirebaseAppConfigProvider) bind Initializable::class
        single<AppConfigProvider> { get<FirebaseAppConfigProvider>() }
        single<AppConfigRepo> { DefaultAppConfigRepo(get(), get()) }
    }
