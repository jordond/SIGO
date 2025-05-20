package app.sigot.location

import app.sigot.core.domain.location.LocationRepo
import app.sigot.location.data.DefaultLocationRepo
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun locationModule(): Module =
    module {
        factoryOf(::DefaultLocationRepo) bind LocationRepo::class
    }
