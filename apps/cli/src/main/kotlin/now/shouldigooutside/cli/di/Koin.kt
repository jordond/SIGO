package now.shouldigooutside.cli.di

import now.shouldigooutside.core.foundation.di.foundationModule
import now.shouldigooutside.core.platform.di.networkModule
import now.shouldigooutside.core.platform.di.platformModule
import now.shouldigooutside.forecast.forecastCliModule
import now.shouldigooutside.settings.settingsModule
import org.koin.core.Koin
import org.koin.core.context.startKoin

fun initKoin(): Koin =
    startKoin {
        modules(
            // Core
            foundationModule(),
            networkModule(),
            platformModule(),
            // Feature
            settingsModule(useStore = false),
            forecastCliModule(),
            cliModule(),
        )
    }.koin
