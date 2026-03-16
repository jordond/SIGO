package now.shouldigooutside.forecast.ui.components.mappers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import now.shouldigooutside.core.model.ForecastData
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
internal fun ForecastData?.rememberInstant(): Instant =
    remember(this) {
        this?.forecast?.instant ?: Clock.System.now()
    }
