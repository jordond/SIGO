package app.sigot.cli.di

import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.di.platformModule
import app.sigot.forecast.forecastCliModule
import app.sigot.settings.settingsModule
import org.koin.core.Koin
import org.koin.core.context.startKoin

fun initKoin(): Koin =
    startKoin {
        modules(
            // Core
            foundationModule(),
            platformModule(),
            // Feature
            settingsModule(useStore = false),
            forecastCliModule(),
            cliModule(),
        )
    }.koin
