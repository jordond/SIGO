package now.shouldigooutside.core.widget

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public actual fun widgetModule(): Module =
    module {
        single { IosWidgetDataStore() } bind WidgetDataStore::class
        single { IosWidgetNotifier() } bind WidgetNotifier::class
    }
