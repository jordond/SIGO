package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.cache.ApiCache
import kotlin.time.Duration

/**
 * Per-request hook for fire-and-forget background work that must outlive the
 * HTTP response. On Cloudflare Workers this is backed by `ctx.waitUntil`; on
 * the JVM the work is run inline as a regular coroutine.
 */
public fun interface ExecutionContext {
    public fun waitUntil(block: suspend () -> Unit)
}

/**
 * Schedules a `cache.put` to run after the response has been flushed. No-op
 * when [cache] is null.
 */
public fun ExecutionContext.putDeferred(
    cache: ApiCache?,
    key: String,
    value: String,
    ttl: Duration,
) {
    if (cache != null) waitUntil { cache.put(key, value, ttl) }
}
