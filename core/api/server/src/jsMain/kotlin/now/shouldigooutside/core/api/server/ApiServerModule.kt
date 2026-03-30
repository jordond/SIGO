package now.shouldigooutside.core.api.server

import org.koin.core.module.Module
import org.koin.dsl.module

public fun jsApiServerModule(): Module =
    module {
        includes(commonApiServerModule())
    }
