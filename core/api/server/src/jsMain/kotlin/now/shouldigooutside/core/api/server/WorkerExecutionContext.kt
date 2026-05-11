package now.shouldigooutside.core.api.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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

    // Each waitUntil gets a fresh SupervisorJob+Unconfined scope so completed
    // coroutines can release their Job/Continuation references immediately.
    // A shared module-level scope accumulates child Jobs across requests under
    // burst load, which correlates with workerd dropping new requests at
    // admission with wallTime=0 ms and no user logs.
    override fun waitUntil(block: suspend () -> Unit) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        ctx.waitUntil(
            scope.promise {
                try {
                    block()
                } finally {
                    scope.cancel()
                }
            },
        )
    }
}
