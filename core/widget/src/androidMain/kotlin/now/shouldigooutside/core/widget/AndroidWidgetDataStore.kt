package now.shouldigooutside.core.widget

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.json.Json

private const val PREFS_NAME = "sigo_widget_data"
private const val KEY_WIDGET_DATA = "widget_data_json"

public class AndroidWidgetDataStore(
    context: Context,
) : WidgetDataStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun save(data: WidgetData) {
        val json = Json.encodeToString(WidgetData.serializer(), data)
        prefs.edit().putString(KEY_WIDGET_DATA, json).apply()
    }

    override fun load(): WidgetData? {
        val json = prefs.getString(KEY_WIDGET_DATA, null) ?: return null
        return try {
            Json.decodeFromString(WidgetData.serializer(), json)
        } catch (_: Exception) {
            null
        }
    }
}
