package now.shouldigooutside.core.widget

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public actual fun widgetModule(): Module =
    module {
        includes(commonWidgetModule())
        single { AndroidWidgetDataStore(get()) } bind WidgetDataStore::class
        single { AndroidWidgetNotifier() } bind WidgetNotifier::class
    }
