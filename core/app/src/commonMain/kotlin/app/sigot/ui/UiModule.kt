package app.sigot.ui

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun uiModule(): Module =
    module {
        viewModelOf(::AppHostModel)
    }
