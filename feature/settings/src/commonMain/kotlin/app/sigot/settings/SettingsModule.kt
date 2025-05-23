package app.sigot.settings

import app.sigot.core.domain.settings.HapticsUseCase
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.settings.Settings
import app.sigot.core.platform.store.NoopStore
import app.sigot.core.platform.store.Store
import app.sigot.settings.data.KeyValueSettingsRepo
import app.sigot.settings.data.entity.toEntity
import app.sigot.settings.domain.DefaultHapticsUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun settingsModule(useStore: Boolean = true): Module =
    module {
        single<SettingsRepo> {
            val store = if (!useStore) {
                NoopStore()
            } else {
                Store.storeOf(
                    filename = "app-settings.json",
                    type = Store.Type.Persistent,
                    default = Settings().toEntity(),
                )
            }

            KeyValueSettingsRepo(store, get())
        }

        factoryOf(::DefaultHapticsUseCase) bind HapticsUseCase::class
    }
