package app.sigot.core.platform.di

import dev.jordond.connectivity.Connectivity
import org.koin.core.module.Module
import org.koin.dsl.module

public fun platformModule(): Module =
    module {
        platformConfig()

        single<Connectivity> { getConnectivity() }
    }

internal expect fun Module.platformConfig()

internal expect fun getConnectivity(): Connectivity
