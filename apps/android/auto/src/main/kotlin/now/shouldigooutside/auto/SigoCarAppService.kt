package now.shouldigooutside.auto

import android.content.pm.ApplicationInfo
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import now.shouldigooutside.auto.di.AutoStringsProvider
import now.shouldigooutside.auto.di.CarLocationProviderFactory
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SigoCarAppService :
    CarAppService(),
    KoinComponent {
    private val forecastStateHolder: ForecastStateHolder by inject()
    private val settingsRepo: SettingsRepo by inject()
    private val getActivitiesScoreUseCase: GetActivitiesScoreUseCase by inject()
    private val stringsProvider: AutoStringsProvider by inject()
    private val carLocationProviderFactory: CarLocationProviderFactory by inject()

    override fun createHostValidator(): HostValidator =
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            HostValidator
                .Builder(this)
                .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                .build()
        }

    override fun onCreateSession(): Session =
        SigoSession(
            deps = SigoSessionDeps(
                forecastStateHolder = forecastStateHolder,
                settingsRepo = settingsRepo,
                getActivitiesScoreUseCase = getActivitiesScoreUseCase,
                carLocationProviderFactory = carLocationProviderFactory,
                stringsProvider = stringsProvider,
            ),
        )
}
