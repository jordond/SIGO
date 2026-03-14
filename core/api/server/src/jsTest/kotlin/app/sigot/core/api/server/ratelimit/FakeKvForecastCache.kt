package app.sigot.core.api.server.ratelimit

import app.sigot.core.api.server.cache.KvForecastCache
import kotlin.js.Promise

/**
 * Creates a [KvForecastCache] backed by an in-memory map for testing.
 */
internal fun fakeKvCache(): KvForecastCache {
    val store = mutableMapOf<String, String>()

    val fakeKv: dynamic = js("({})")
    fakeKv.get = { key: String ->
        Promise.resolve(store[key])
    }
    fakeKv.put = { key: String, value: String, _: dynamic ->
        store[key] = value
        Promise.resolve(Unit)
    }

    return KvForecastCache(fakeKv)
}
