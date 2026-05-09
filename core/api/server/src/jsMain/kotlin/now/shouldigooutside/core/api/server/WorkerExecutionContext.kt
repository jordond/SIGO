package now.shouldigooutside.core.api.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.promise

/**
 * Cloudflare Worker [ExecutionContext] backed by the runtime-provided
 * `ctx.waitUntil`. The runtime keeps the request alive — and the V8 isolate
 * billable — until the supplied promise settles, so background writes (KV
 * puts, telemetry) finish even after the HTTP response is flushed.
 *
 * `ctx` is the dynamic Cloudflare object; we only call `waitUntil` on it.
 */
public class WorkerExecutionContext(
    private val ctx: dynamic,
) : ExecutionContext {
    init {
        require(ctx != null && ctx != undefined) {
            "WorkerExecutionContext requires the Cloudflare execution context (ctx)"
        }
    }

    override fun waitUntil(block: suspend () -> Unit) {
        ctx.waitUntil(workerScope.promise { block() })
    }
}

// Isolate-scoped scope for fire-and-forget waitUntil work. SupervisorJob keeps
// failures from cascading; Dispatchers.Unconfined avoids cross-request
// microtask hops on Workers (continuations stay on the resolving Promise).
private val workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
