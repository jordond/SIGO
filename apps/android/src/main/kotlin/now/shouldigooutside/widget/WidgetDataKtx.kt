package now.shouldigooutside.widget

import android.content.Context
import now.shouldigooutside.R
import now.shouldigooutside.core.widget.WidgetData

internal fun WidgetData.updatedAgo(context: Context): String {
    val minutes = updatedAgoMinutes
    return if (minutes < 60) {
        context.getString(R.string.widget_updated_ago_minutes, minutes.toInt())
    } else {
        context.getString(R.string.widget_updated_ago_hours, (minutes / 60).toInt())
    }
}
