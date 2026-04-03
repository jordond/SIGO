package now.shouldigooutside.core.platform

import android.app.Activity
import java.lang.ref.WeakReference

public object ActivityProvider {
    private var activityRef: WeakReference<Activity>? = null

    public fun set(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    public fun clear() {
        activityRef?.clear()
        activityRef = null
    }

    internal fun get(): Activity? = activityRef?.get()
}
