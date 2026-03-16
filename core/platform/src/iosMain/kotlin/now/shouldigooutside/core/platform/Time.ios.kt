package now.shouldigooutside.core.platform

import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.CLOCK_REALTIME
import platform.posix.clock_gettime
import platform.posix.timespec

public actual fun currentTimeMillis(): Long =
    memScoped {
        val timeHolder = alloc<timespec>()
        clock_gettime(CLOCK_REALTIME.convert(), timeHolder.ptr)
        timeHolder.tv_sec * 1000L + timeHolder.tv_nsec / 1000000L
    }
