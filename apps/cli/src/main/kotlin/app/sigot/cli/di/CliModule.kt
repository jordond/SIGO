package app.sigot.cli.di

import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.di.platformModule
import app.sigot.forecast.data.forecastDataModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(): KoinApplication =
    startKoin {
        modules(
            // Core
            foundationModule(),
            platformModule(),
            // Feature
            forecastDataModule(),
        )
    }
