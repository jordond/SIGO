package now.shouldigooutside.core.config

import now.shouldigooutside.core.config.firebase.FirebaseAppConfigProvider
import now.shouldigooutside.core.model.Initializable
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
