package now.shouldigooutside.forecast

import now.shouldigooutside.forecast.data.source.visualcrossing.NativeFetchVisualCrossingApi
import now.shouldigooutside.forecast.data.source.visualcrossing.VisualCrossingApi
import org.koin.dsl.ScopeDSL

/**
 * Registers a [VisualCrossingApi] backed by `globalThis.fetch` instead of
 * `ktor-client-js`. Pair with [scopedForecastDefinitions] in JS targets
 * (specifically Cloudflare Workers) where the Ktor JS engine hangs under
 * concurrent load.
 */
public fun ScopeDSL.scopedJsNativeVisualCrossingApi() {
    scoped<VisualCrossingApi> { NativeFetchVisualCrossingApi(get(), get()) }
}
