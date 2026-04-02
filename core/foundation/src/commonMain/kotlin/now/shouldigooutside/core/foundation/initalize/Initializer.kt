package now.shouldigooutside.core.foundation.initalize

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import now.shouldigooutside.core.model.Initializable
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
            currentCoroutineContext().ensureActive()
            item.initialize()
        }
        didInitialize = true
    }
}
