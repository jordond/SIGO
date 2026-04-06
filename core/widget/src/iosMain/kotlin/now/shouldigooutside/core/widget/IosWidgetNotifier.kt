package now.shouldigooutside.core.widget

public class IosWidgetNotifier : WidgetNotifier {
    override fun notifyUpdate() {
        onUpdate?.invoke()
    }

    public companion object {
        public var onUpdate: (() -> Unit)? = null
    }
}
