package now.shouldigooutside.core.widget

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

public class AndroidWidgetNotifier : WidgetNotifier {
    private val _updates = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val updates: SharedFlow<Unit> = _updates.asSharedFlow()

    override fun notifyUpdate() {
        _updates.tryEmit(Unit)
    }
}
