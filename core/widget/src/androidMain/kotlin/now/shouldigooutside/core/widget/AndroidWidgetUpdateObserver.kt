package now.shouldigooutside.core.widget

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch

/**
 * Bridges [WidgetNotifier.updates] to a callback that refreshes Glance widgets.
 * Rapid emissions are conflated so downstream `updateAll` calls coalesce.
 */
public class AndroidWidgetUpdateObserver(
    private val notifier: WidgetNotifier,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    public fun start(onUpdate: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch {
            notifier.updates.conflate().collect { onUpdate() }
        }
    }

    public fun stop() {
        job?.cancel()
        job = null
    }
}
