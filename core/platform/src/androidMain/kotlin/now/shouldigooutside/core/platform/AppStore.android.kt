package now.shouldigooutside.core.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import now.shouldigooutside.core.platform.di.getKoinInstance

public actual val appIdentifier: String
    get() = getKoinInstance<Context>().applicationContext.packageName

public actual fun launchAppStore() {
    val context = getKoinInstance<Context>()

    val uri = "market://details?id=$appIdentifier".toUri()
    val flags =
        Intent.FLAG_ACTIVITY_NO_HISTORY or
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
            Intent.FLAG_ACTIVITY_NEW_TASK

    val intent = Intent(Intent.ACTION_VIEW, uri).apply { addFlags(flags) }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val backup =
            "http://play.google.com/store/apps/details?id=$appIdentifier".toUri()
        context.startActivity(Intent(Intent.ACTION_VIEW, backup))
    }
}
