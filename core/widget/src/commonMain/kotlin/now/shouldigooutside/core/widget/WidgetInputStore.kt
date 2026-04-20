package now.shouldigooutside.core.widget

public interface WidgetInputStore {
    public fun save(inputs: WidgetInputs)

    public fun load(): WidgetInputs?
}

internal object NoOpWidgetInputStore : WidgetInputStore {
    override fun save(inputs: WidgetInputs) = Unit

    override fun load(): WidgetInputs? = null
}
