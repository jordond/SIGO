package now.shouldigooutside.core.widget

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

public expect fun widgetModule(): Module

internal fun commonWidgetModule(): Module =
    module {
        factoryOf(::UpdateWidgetDataUseCase)
    }
