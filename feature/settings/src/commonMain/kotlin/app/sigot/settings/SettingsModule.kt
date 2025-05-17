package app.sigot.settings

import app.sigot.core.domain.settings.HapticsUseCase
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.settings.Settings
import app.sigot.core.platform.store.createPersistentStore
import app.sigot.settings.data.KeyValueSettingsRepo
import app.sigot.settings.data.entity.SettingsEntity
import app.sigot.settings.data.entity.toEntity
import app.sigot.settings.domain.DefaultHapticsUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun settingsModule(): Module =
    module {
        single<SettingsRepo> {
            val store = createPersistentStore<SettingsEntity>(
                filename = "app-settings.json",
                default = Settings().toEntity(),
            )

            KeyValueSettingsRepo(store, get())
        }

        factoryOf(::DefaultHapticsUseCase) bind HapticsUseCase::class
    }
