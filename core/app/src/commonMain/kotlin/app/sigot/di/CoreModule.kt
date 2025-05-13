package app.sigot.di

import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.di.platformModule
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun coreModule(): Module =
    module {
        includes(
            foundationModule(),
            platformModule(),
        )
    }
