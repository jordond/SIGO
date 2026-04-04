package now.shouldigooutside.core.widget

import platform.Foundation.NSUserDefaults

private const val SUITE_NAME = "group.now.shouldigooutside"
private const val KEY_WIDGET_DATA = "widget_data_json"

public class IosWidgetDataStore : WidgetDataStore {
    private val defaults: NSUserDefaults? = NSUserDefaults(suiteName = SUITE_NAME)

    override fun save(data: WidgetData) {
        val encoded = WidgetDataStore.json.encodeToString(WidgetData.serializer(), data)
        defaults?.setObject(encoded, forKey = KEY_WIDGET_DATA)
        defaults?.synchronize()
    }

    override fun load(): WidgetData? {
        val encoded = defaults?.stringForKey(KEY_WIDGET_DATA) ?: return null
        return try {
            WidgetDataStore.json.decodeFromString(WidgetData.serializer(), encoded)
        } catch (_: Exception) {
            null
        }
    }
}
