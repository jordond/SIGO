package now.shouldigooutside

import android.app.Application
import androidx.glance.appwidget.updateAll
import now.shouldigooutside.core.widget.AndroidWidgetUpdateObserver
import now.shouldigooutside.di.initKoin
import now.shouldigooutside.widget.SigoWidget
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainApplication :
    Application(),
    KoinComponent {
    private val widget = SigoWidget()
    private val widgetUpdateObserver: AndroidWidgetUpdateObserver by inject()

    override fun onCreate() {
        super.onCreate()

        instance = this

        initKoin {
            androidContext(this@MainApplication)
        }

        widgetUpdateObserver.start { widget.updateAll(this@MainApplication) }
    }

    companion object {
        lateinit var instance: Application
    }
}
