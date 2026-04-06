package now.shouldigooutside.core.widget

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.json.Json

private const val PREFS_NAME = "sigo_widget_data"
private const val KEY_WIDGET_DATA = "widget_data_json"

public class AndroidWidgetDataStore(
    context: Context,
    private val json: Json,
) : WidgetDataStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun save(data: WidgetData) {
        val encoded = json.encodeToString<WidgetData>(data)
        prefs.edit { putString(KEY_WIDGET_DATA, encoded) }
    }

    override fun load(): WidgetData? {
        val encoded = prefs.getString(KEY_WIDGET_DATA, null) ?: return null
        return try {
            json.decodeFromString<WidgetData>(encoded)
        } catch (_: Exception) {
            null
        }
    }
}
