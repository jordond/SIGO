package now.shouldigooutside.core.widget

import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

private const val SUITE_NAME = "group.now.shouldigooutside"
private const val KEY_WIDGET_DATA = "widget_data_json"

public class IosWidgetDataStore : WidgetDataStore {
    private val defaults: NSUserDefaults? = NSUserDefaults(suiteName = SUITE_NAME)

    override fun save(data: WidgetData) {
        val json = Json.encodeToString(WidgetData.serializer(), data)
        defaults?.setObject(json, forKey = KEY_WIDGET_DATA)
        defaults?.synchronize()
    }

    override fun load(): WidgetData? {
        val json = defaults?.stringForKey(KEY_WIDGET_DATA) ?: return null
        return try {
            Json.decodeFromString(WidgetData.serializer(), json)
        } catch (_: Exception) {
            null
        }
    }
}
