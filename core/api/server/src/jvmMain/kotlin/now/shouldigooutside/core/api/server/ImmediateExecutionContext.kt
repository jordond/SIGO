package now.shouldigooutside.core.api.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * JVM [ExecutionContext] that runs background work on a long-lived application
 * scope. Unlike Cloudflare Workers, the JVM server has no per-request
 * lifecycle to extend, so the work is launched immediately and runs to
 * completion regardless of the originating request.
 */
public class ImmediateExecutionContext(
    private val scope: CoroutineScope,
) : ExecutionContext {
    override fun waitUntil(block: suspend () -> Unit) {
        scope.launch { block() }
    }
}
