package now.shouldigooutside.core.platform

import android.content.Context
import android.content.pm.ApplicationInfo
import org.koin.mp.KoinPlatformTools

public actual val isDebug: Boolean by lazy {
    try {
        val ctx = KoinPlatformTools.defaultContext().get().get<Context>()
        (ctx.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    } catch (_: Exception) {
        false
    }
}
