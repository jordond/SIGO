package now.shouldigooutside.core.widget

import kotlinx.coroutines.flow.SharedFlow

public interface WidgetNotifier {
    public val updates: SharedFlow<Unit>

    public fun notifyUpdate()
}
