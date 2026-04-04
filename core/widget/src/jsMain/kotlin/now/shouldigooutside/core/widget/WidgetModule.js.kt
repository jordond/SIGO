package now.shouldigooutside.core.widget

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public actual fun widgetModule(): Module =
    module {
        includes(commonWidgetModule())
        single { NoOpWidgetDataStore() } bind WidgetDataStore::class
        single { NoOpWidgetNotifier() } bind WidgetNotifier::class
    }

private class NoOpWidgetDataStore : WidgetDataStore {
    override fun save(data: WidgetData) = Unit

    override fun load(): WidgetData? = null

    override fun saveConfig(config: WidgetConfig) = Unit

    override fun loadConfig(): WidgetConfig? = null
}

private class NoOpWidgetNotifier : WidgetNotifier {
    override fun notifyUpdate() = Unit
}
