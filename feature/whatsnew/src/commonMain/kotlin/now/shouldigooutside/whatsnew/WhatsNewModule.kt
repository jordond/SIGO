package now.shouldigooutside.whatsnew

import now.shouldigooutside.core.model.Initializable
import now.shouldigooutside.core.platform.store.Store
import now.shouldigooutside.whatsnew.data.KeyValueWhatsNewRepo
import now.shouldigooutside.whatsnew.data.WhatsNewRegistry
import now.shouldigooutside.whatsnew.data.WhatsNewRepo
import now.shouldigooutside.whatsnew.data.entity.WhatsNewStateEntity
import now.shouldigooutside.whatsnew.ui.WhatsNewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.binds
import org.koin.dsl.module

public fun whatsNewModule(): Module =
    module {
        single {
            val store = Store.storeOf(
                filename = "whats-new-state.json",
                type = Store.Type.Persistent,
                default = WhatsNewStateEntity(),
            )
            KeyValueWhatsNewRepo(
                store = store,
                registry = WhatsNewRegistry,
                settingsRepo = get(),
                versionProvider = get(),
                scope = get(),
            )
        } binds arrayOf(WhatsNewRepo::class, Initializable::class)

        viewModelOf(::WhatsNewModel)
    }
