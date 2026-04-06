package now.shouldigooutside

import android.app.Application
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import now.shouldigooutside.core.widget.AndroidWidgetNotifier
import now.shouldigooutside.di.initKoin
import now.shouldigooutside.widget.SigoWidget
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        instance = this

        AndroidWidgetNotifier.onUpdate = {
            applicationScope.launch { SigoWidget().updateAll(this@MainApplication) }
        }

        initKoin {
            androidContext(this@MainApplication)
        }
    }

    companion object {
        lateinit var instance: Application
    }
}
