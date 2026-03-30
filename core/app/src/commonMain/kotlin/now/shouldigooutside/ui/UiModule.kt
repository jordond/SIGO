package now.shouldigooutside.ui

import now.shouldigooutside.ui.home.HomeModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun uiModule(): Module =
    module {
        viewModelOf(::AppHostModel)
        viewModelOf(::HomeModel)
    }
