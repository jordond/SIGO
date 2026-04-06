package now.shouldigooutside.core.widget

public class AndroidWidgetNotifier : WidgetNotifier {
    override fun notifyUpdate() {
        onUpdate?.invoke()
    }

    public companion object {
        public var onUpdate: (() -> Unit)? = null
    }
}
