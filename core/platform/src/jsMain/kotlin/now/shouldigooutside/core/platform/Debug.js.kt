package now.shouldigooutside.core.platform

public actual val isDebug: Boolean
    get() = runCatching { js("process.env.NODE_ENV !== 'production'") as Boolean }
        .getOrDefault(false)
