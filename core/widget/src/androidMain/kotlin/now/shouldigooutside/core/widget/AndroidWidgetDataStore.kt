package now.shouldigooutside.core.widget

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "sigo_widget_data"
private const val KEY_WIDGET_DATA = "widget_data_json"

public class AndroidWidgetDataStore(
    context: Context,
) : WidgetDataStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun save(data: WidgetData) {
        val encoded = WidgetDataStore.json.encodeToString(WidgetData.serializer(), data)
        prefs.edit().putString(KEY_WIDGET_DATA, encoded).apply()
    }

    override fun load(): WidgetData? {
        val encoded = prefs.getString(KEY_WIDGET_DATA, null) ?: return null
        return try {
            WidgetDataStore.json.decodeFromString(WidgetData.serializer(), encoded)
        } catch (_: Exception) {
            null
        }
    }
}
