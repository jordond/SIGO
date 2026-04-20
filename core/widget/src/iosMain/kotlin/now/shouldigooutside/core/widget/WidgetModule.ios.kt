package now.shouldigooutside.core.widget

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public actual fun widgetModule(): Module =
    module {
        includes(commonWidgetModule())
        single { IosWidgetDataStore(get()) } bind WidgetDataStore::class
        single { IosWidgetInputStore(get()) } bind WidgetInputStore::class
        single { IosWidgetNotifier() } bind WidgetNotifier::class
        single { IosWidgetUpdateObserver(get()) }
    }
