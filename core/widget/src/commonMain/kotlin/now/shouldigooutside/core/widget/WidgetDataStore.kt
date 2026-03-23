package now.shouldigooutside.core.widget

public interface WidgetDataStore {
    public fun save(data: WidgetData)

    public fun load(): WidgetData?
}
