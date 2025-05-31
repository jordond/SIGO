package app.sigot.core.platform

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

public fun ticker(duration: Duration): Flow<Unit> =
    flow {
        while (coroutineContext.isActive) {
            emit(Unit)
            delay(duration)
        }
    }
