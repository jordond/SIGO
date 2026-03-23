package now.shouldigooutside.core.widget

public class AndroidWidgetNotifier : WidgetNotifier {
    override fun notifyUpdate() {
        // No-op on Android — widget updates are handled by WorkManager
        // and direct GlanceAppWidget.updateAll() calls from the widget receiver.
    }
}
