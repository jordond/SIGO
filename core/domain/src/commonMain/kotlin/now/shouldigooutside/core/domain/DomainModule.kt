package now.shouldigooutside.core.domain

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun domainModule(): Module =
    module {
        singleOf(::DefaultAppStateHolder) bind AppStateHolder::class
    }
