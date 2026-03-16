package now.shouldigooutside.core.api.server.cache

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

public val FORECAST_CACHE_TTL: Duration = 15.minutes

public interface ApiCache {
    public suspend fun get(key: String): String?

    public suspend fun put(
        key: String,
        value: String,
        ttl: Duration,
    )
}
