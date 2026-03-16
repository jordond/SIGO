package now.shouldigooutside.location

import now.shouldigooutside.core.domain.location.FetchLocationUseCase
import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.domain.location.SearchLocationUseCase
import now.shouldigooutside.location.data.DefaultLocationRepo
import now.shouldigooutside.location.domain.DefaultFetchLocationUseCase
import now.shouldigooutside.location.domain.DefaultSearchLocationUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun locationModule(): Module =
    module {
        factoryOf(::DefaultLocationRepo) bind LocationRepo::class
        factoryOf(::DefaultFetchLocationUseCase) bind FetchLocationUseCase::class
        factoryOf(::DefaultSearchLocationUseCase) bind SearchLocationUseCase::class
    }
