package now.shouldigooutside.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import now.shouldigooutside.core.config.AppConfigRepo
import now.shouldigooutside.core.config.model.AppConfig
import now.shouldigooutside.core.domain.forecast.ForecastRepo
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.domain.settings.IsSimulateFailureUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.NowProvider
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.location.LocationPermissionStatus
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Duration
import kotlin.time.Instant

public class FakeSettingsRepo(
    initial: Settings = Settings(firstLaunch = Instant.fromEpochSeconds(0)),
) : SettingsRepo {
    private val _settings = MutableStateFlow(initial)
    override val settings: StateFlow<Settings> = _settings.asStateFlow()

    private val default: Settings = initial

    override fun update(block: (Settings) -> Settings) {
        _settings.value = block(_settings.value)
    }

    override fun reset() {
        _settings.value = default
    }
}

public class FakeScoreCalculator(
    public var result: ForecastScore = testForecastScore(),
) : ScoreCalculator {
    public var lastForecast: Forecast? = null
    public var lastPreferences: Preferences? = null
    public var lastIncludeAirQuality: Boolean? = null

    override fun calculate(
        forecast: Forecast,
        preferences: Preferences,
        includeAirQuality: Boolean,
    ): ForecastScore {
        lastForecast = forecast
        lastPreferences = preferences
        lastIncludeAirQuality = includeAirQuality
        return result
    }
}

public class FakeLocationRepo(
    public var locationResult: LocationResult = LocationResult.Success(testLocation()),
    public var permissionStatus: LocationPermissionStatus = LocationPermissionStatus.Granted,
) : LocationRepo {
    override fun hasPermission(): Boolean = permissionStatus == LocationPermissionStatus.Granted

    override suspend fun requestPermission(): LocationPermissionStatus = permissionStatus

    override suspend fun location(): LocationResult = locationResult
}

public class FakeGetForecastUseCase(
    public var result: Result<Forecast> = Result.success(testForecast()),
) : GetForecastUseCase {
    public var lastLocationObj: Location? = null
    public var lastLocationStr: String? = null
    public var lastUnits: Units? = null

    override suspend fun forecastFor(
        location: Location,
        units: Units?,
    ): Result<Forecast> {
        lastLocationObj = location
        lastUnits = units
        return result
    }

    override suspend fun forecastFor(
        location: String,
        units: Units?,
    ): Result<Forecast> {
        lastLocationStr = location
        lastUnits = units
        return result
    }
}

public class FakeForecastRepo(
    public var result: Result<Forecast> = Result.success(testForecast()),
) : ForecastRepo {
    public var lastLocationObj: Location? = null
    public var lastLocationStr: String? = null
    public var lastForce: Boolean? = null

    override suspend fun forecastFor(
        location: Location,
        force: Boolean,
    ): Result<Forecast> {
        lastLocationObj = location
        lastForce = force
        return result
    }

    override suspend fun forecastFor(
        location: String,
        force: Boolean,
    ): Result<Forecast> {
        lastLocationStr = location
        lastForce = force
        return result
    }
}

public class FakeAppConfigRepo(
    initial: AppConfig = AppConfig(),
) : AppConfigRepo {
    private val _config = MutableStateFlow(initial)
    override val config: StateFlow<AppConfig> = _config.asStateFlow()

    public fun update(block: (AppConfig) -> AppConfig) {
        _config.value = block(_config.value)
    }

    public fun set(config: AppConfig) {
        _config.value = config
    }
}

public class FakeNowProvider(
    public var instant: Instant = Instant.fromEpochSeconds(0),
) : NowProvider {
    override fun now(): Instant = instant

    override fun today(): LocalDate = instant.toLocalDate()

    override fun todayFlow(): Flow<LocalDate> = flow { emit(today()) }

    override fun durationFromNow(instant: Instant): Duration = now().minus(instant)
}

private fun Instant.toLocalDate(): LocalDate = this.toLocalDateTime(TimeZone.UTC).date

public class FakeIsSimulateFailureUseCase(
    public var shouldFail: Boolean = false,
) : IsSimulateFailureUseCase {
    override fun invoke(): Boolean = shouldFail
}
