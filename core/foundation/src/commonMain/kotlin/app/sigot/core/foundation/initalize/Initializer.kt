package app.sigot.core.foundation.initalize

import app.sigot.core.model.Initializable
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

public interface Initializer {
    public suspend fun initialize()
}

internal class DefaultInitializer(
    private val items: List<Initializable>,
) : Initializer {
    private var didInitialize = false

    override suspend fun initialize() {
        if (didInitialize) return

        for (item in items) {
            coroutineContext.ensureActive()
            item.initialize()
        }
        didInitialize = true
    }
}
