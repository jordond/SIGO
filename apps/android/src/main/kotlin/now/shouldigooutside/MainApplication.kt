package now.shouldigooutside

import android.app.Application
import app.sigot.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this

        initKoin {
            androidContext(this@MainApplication)
        }
    }

    companion object {
        lateinit var instance: Application
    }
}
