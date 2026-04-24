package now.shouldigooutside.core.widget

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public actual fun widgetModule(): Module =
    module {
        includes(commonWidgetModule())
        singleOf(::AndroidWidgetDataStore) bind WidgetDataStore::class
        single { NoOpWidgetInputStore } bind WidgetInputStore::class
        singleOf(::AndroidWidgetNotifier) bind WidgetNotifier::class
        singleOf(::AndroidWidgetUpdateObserver)
    }
