package now.shouldigooutside.settings

import now.shouldigooutside.core.domain.GetPreferenceRangesUseCase
import now.shouldigooutside.core.domain.settings.HapticsUseCase
import now.shouldigooutside.core.domain.settings.IsSimulateFailureUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.platform.store.NoopStore
import now.shouldigooutside.core.platform.store.Store
import now.shouldigooutside.settings.data.KeyValueSettingsRepo
import now.shouldigooutside.settings.data.entity.toEntity
import now.shouldigooutside.settings.domain.DefaultGetPreferenceRangesUseCase
import now.shouldigooutside.settings.domain.DefaultHapticsUseCase
import now.shouldigooutside.settings.domain.SettingsIsSimulateFailureUseCase
import now.shouldigooutside.settings.ui.SettingsModel
import now.shouldigooutside.settings.ui.internal.InternalSettingsModel
import now.shouldigooutside.settings.ui.preferences.PreferencesModel
import now.shouldigooutside.settings.ui.units.UnitsModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
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

        factoryOf(::SettingsIsSimulateFailureUseCase) bind IsSimulateFailureUseCase::class
        factoryOf(::DefaultHapticsUseCase) bind HapticsUseCase::class
        factoryOf(::DefaultGetPreferenceRangesUseCase) bind GetPreferenceRangesUseCase::class

        viewModelOf(::SettingsModel)
        viewModelOf(::InternalSettingsModel)
        viewModelOf(::UnitsModel)
        viewModelOf(::PreferencesModel)
    }
