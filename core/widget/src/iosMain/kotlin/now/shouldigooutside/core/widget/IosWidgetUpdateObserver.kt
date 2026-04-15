package now.shouldigooutside.core.widget

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Bridges [WidgetNotifier.updates] to a Swift callback.
 * Call [start] from iOS app init with a closure that reloads widget timelines.
 */
public class IosWidgetUpdateObserver(
    private val notifier: WidgetNotifier,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var job: Job? = null

    public fun start(onUpdate: () -> Unit) {
        job?.cancel()
        job = scope.launch {
            notifier.updates.collect { onUpdate() }
        }
    }

    public fun stop() {
        job?.cancel()
        job = null
    }
}
