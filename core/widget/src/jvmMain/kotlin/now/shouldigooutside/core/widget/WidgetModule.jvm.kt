package now.shouldigooutside.core.widget

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public actual fun widgetModule(): Module =
    module {
        includes(commonWidgetModule())
        single { NoOpWidgetDataStore } bind WidgetDataStore::class
        single { NoOpWidgetInputStore } bind WidgetInputStore::class
        single { NoOpWidgetNotifier() } bind WidgetNotifier::class
    }

private object NoOpWidgetDataStore : WidgetDataStore {
    override fun save(data: WidgetData) = Unit

    override fun load(): WidgetData? = null
}

private class NoOpWidgetNotifier : WidgetNotifier {
    override val updates: SharedFlow<Unit> = MutableSharedFlow<Unit>().asSharedFlow()

    override fun notifyUpdate(): Unit = Unit
}
