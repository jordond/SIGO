package app.sigot.core.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public actual val Dispatchers.IO: CoroutineDispatcher
    get() = Dispatchers.Default
