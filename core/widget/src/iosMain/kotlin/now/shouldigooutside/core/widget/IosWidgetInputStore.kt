package now.shouldigooutside.core.widget

import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

private const val SUITE_NAME = "group.now.shouldigooutside"
private const val KEY_WIDGET_INPUTS = "widget_inputs_json"

public class IosWidgetInputStore(
    private val json: Json,
) : WidgetInputStore {
    private val defaults: NSUserDefaults = NSUserDefaults(suiteName = SUITE_NAME)

    override fun save(inputs: WidgetInputs) {
        val encoded = json.encodeToString(WidgetInputs.serializer(), inputs)
        defaults.setObject(encoded, forKey = KEY_WIDGET_INPUTS)
        defaults.synchronize()
    }

    override fun load(): WidgetInputs? {
        val encoded = defaults.stringForKey(KEY_WIDGET_INPUTS) ?: return null
        return try {
            json.decodeFromString(WidgetInputs.serializer(), encoded)
        } catch (_: Exception) {
            null
        }
    }
}
