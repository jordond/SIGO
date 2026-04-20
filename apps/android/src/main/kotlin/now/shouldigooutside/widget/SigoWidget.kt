package now.shouldigooutside.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import now.shouldigooutside.MainActivity
import now.shouldigooutside.core.widget.WidgetDataMapper
import now.shouldigooutside.core.widget.WidgetDataStore
import now.shouldigooutside.core.widget.WidgetStrings
import now.shouldigooutside.core.widget.resolveAlerts
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SigoWidget :
    GlanceAppWidget(),
    KoinComponent {
    private val widgetDataStore: WidgetDataStore by inject()

    companion object {
        private val ROW_SHORT = DpSize(170.dp, 50.dp)
        private val ROW_WIDE = DpSize(300.dp, 50.dp)
        private val SMALL = DpSize(100.dp, 100.dp)
        private val MEDIUM = DpSize(250.dp, 100.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(ROW_SHORT, ROW_WIDE, SMALL, MEDIUM),
    )

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val stored = widgetDataStore.load()
        val widgetData = stored?.let { WidgetDataMapper.withFreshness(it) }
        val strings = WidgetStrings.resolve()
        val alertsText = widgetData?.resolveAlerts()

        provideContent {
            val isDark = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

            val size = LocalSize.current
            val isRow = size.height < SMALL.height
            val isWide = size.width >= MEDIUM.width

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>()),
            ) {
                when {
                    isRow -> RowWidgetContent(
                        data = widgetData,
                        strings = strings,
                        isDark = isDark,
                        wide = isWide,
                    )
                    isWide -> MediumWidgetContent(
                        data = widgetData,
                        strings = strings,
                        alertsText = alertsText,
                        isDark = isDark,
                    )
                    else -> SmallWidgetContent(
                        data = widgetData,
                        strings = strings,
                        isDark = isDark,
                    )
                }
            }
        }
    }
}
