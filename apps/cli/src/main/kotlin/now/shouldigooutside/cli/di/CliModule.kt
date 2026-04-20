package now.shouldigooutside.cli.di

import now.shouldigooutside.cli.config.CliApiTokenProvider
import now.shouldigooutside.cli.config.CliConfig
import now.shouldigooutside.cli.config.CliConfigRepo
import now.shouldigooutside.cli.config.DefaultCliConfigRepo
import now.shouldigooutside.core.domain.forecast.ApiTokenProvider
import now.shouldigooutside.core.platform.store.Store
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun cliModule() =
    module {
        single {
            val store = Store.storeOf<CliConfig>("sigo-cli.json", Store.Type.Persistent)
            DefaultCliConfigRepo(store)
        } bind CliConfigRepo::class

        singleOf(::CliApiTokenProvider) bind ApiTokenProvider::class
    }
