package now.shouldigooutside.auto

import android.content.pm.ApplicationInfo
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import now.shouldigooutside.auto.format.AutoStrings
import now.shouldigooutside.auto.location.CarLocationProvider
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
    private val stringsProvider: suspend () -> AutoStrings by inject()
    private val carLocationProviderFactory: (CarContext) -> CarLocationProvider? by inject()

    override fun createHostValidator(): HostValidator =
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            // TODO(release-validator): replace with the official Android Auto host
            //  signature allowlist before submitting to Play Console.
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
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
