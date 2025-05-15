package app.sigot.settings

import app.sigot.core.domain.settings.HapticsUseCase
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.settings.Settings
import app.sigot.core.platform.store.createPersistentStore
import app.sigot.settings.data.KeyValueSettingsRepo
import app.sigot.settings.data.entity.SettingsEntity
import app.sigot.settings.data.entity.toEntity
import app.sigot.settings.domain.DefaultHapticsUseCase
import app.sigot.settings.domain.GetThemeUseCase
import app.toebean.core.ui.SavedStatesUpdater
import app.toebean.feature.settings.domain.DefaultSavedStatesUpdater
import app.toebean.feature.settings.ui.SettingsModel
import app.toebean.feature.settings.ui.screens.internal.InternalSettingsModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun settingsModule(): Module =
    module {
        single<SettingsRepo> {
            val store = createPersistentStore<SettingsEntity>(
                filename = "app-settings.json",
                default = Settings.default.toEntity(),
            )

            KeyValueSettingsRepo(store, get())
        }

        factoryOf(::DefaultHapticsUseCase) bind HapticsUseCase::class
        factoryOf(::GetThemeUseCase)
        factoryOf(::DefaultSavedStatesUpdater) bind SavedStatesUpdater::class
        viewModelOf(::SettingsModel)

        viewModel {
            InternalSettingsModel(
                petRepo = get(),
                settingsRepo = get(),
                tarotRepo = get(),
                insightsRepo = get(),
                fortuneRepo = get(),
                clearables = getAll(),
                getRequirementStatusUseCase = get(),
            )
        }
    }
