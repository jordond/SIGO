package now.shouldigooutside.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import now.shouldigooutside.MainActivity
import now.shouldigooutside.core.widget.WidgetDataStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SigoWidget :
    GlanceAppWidget(),
    KoinComponent {
    private val widgetDataStore: WidgetDataStore by inject()

    companion object {
        private val SMALL = DpSize(100.dp, 100.dp)
        private val MEDIUM = DpSize(250.dp, 100.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(setOf(SMALL, MEDIUM))

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val widgetData = widgetDataStore.load()

        provideContent {
            val isDark = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

            val size = androidx.glance.LocalSize.current
            val isMedium = size.width >= MEDIUM.width

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>()),
            ) {
                if (isMedium) {
                    MediumWidgetContent(data = widgetData, isDark = isDark)
                } else {
                    SmallWidgetContent(data = widgetData, isDark = isDark)
                }
            }
        }
    }
}
