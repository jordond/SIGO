package now.shouldigooutside.api.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import now.shouldigooutside.core.api.server.scopedApiRouterDefinitions
import now.shouldigooutside.forecast.scopedForecastDefinitions
import now.shouldigooutside.forecast.scopedJsNativeVisualCrossingApi
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.onClose

internal fun requestModule(): Module =
    module {
        scope<RequestScope> {
            // Unconfined keeps continuations on the request's microtask; Default
            // dispatches via setTimeout(0) which workerd does not count as
            // request I/O.
            scoped<CoroutineScope> {
                CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
            } onClose { it?.cancel() }

            scopedForecastDefinitions()
            scopedJsNativeVisualCrossingApi()
            scopedApiRouterDefinitions()
        }
    }
