package now.shouldigooutside.core.widget

import kotlinx.serialization.json.Json

public interface WidgetDataStore {
    public fun save(data: WidgetData)

    public fun load(): WidgetData?

    public companion object {
        public val json: Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
}
