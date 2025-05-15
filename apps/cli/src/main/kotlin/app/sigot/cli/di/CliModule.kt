package app.sigot.cli.di

import app.sigot.cli.config.ApiTokenProvider
import app.sigot.cli.config.CliConfig
import app.sigot.cli.config.CliConfigRepo
import app.sigot.cli.config.DefaultCliConfigRepo
import app.sigot.core.domain.forecast.VisualCrossingTokenProvider
import app.sigot.core.platform.store.Store
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun cliModule() =
    module {
        single {
            val store = Store.storeOf<CliConfig>("config/sigot-cli.json", Store.Type.Persistent)
            DefaultCliConfigRepo(store)
        } bind CliConfigRepo::class

        singleOf(::ApiTokenProvider) bind VisualCrossingTokenProvider::class
    }
