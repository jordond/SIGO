package app.sigot.core.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

public actual val Dispatchers.IO: CoroutineDispatcher get() = Dispatchers.IO
