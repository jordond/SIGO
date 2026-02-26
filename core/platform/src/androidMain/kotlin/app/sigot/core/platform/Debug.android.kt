package app.sigot.core.platform

import android.content.Context
import android.content.pm.ApplicationInfo
import org.koin.core.context.GlobalContext

public actual val isDebug: Boolean by lazy {
    try {
        val ctx = GlobalContext.get().get<Context>()
        (ctx.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    } catch (_: Exception) {
        false
    }
}
