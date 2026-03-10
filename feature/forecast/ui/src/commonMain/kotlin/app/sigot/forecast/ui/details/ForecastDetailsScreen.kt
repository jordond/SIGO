package app.sigot.forecast.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.forecast.ForecastDay
import app.sigot.core.model.location.Location
import app.sigot.core.model.score.ScoreResult
import app.sigot.core.model.units.Units
import app.sigot.core.resources.Res
import app.sigot.core.resources.something_went_wrong
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.ForecastPreviewData
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.toPersistentList
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Composable
internal fun ForecastDetailsScreen(
    onBack: () -> Unit,
    model: ForecastDetailsModel = koinViewModel(),
) {
    val state by model.collectAsState()
    val forecast = state.forecast

    if (forecast == null) {
        Card {
            Column(
                modifier = Modifier.padding(AppTheme.spacing.standard),
            ) {
                Text(text = Res.string.something_went_wrong)
            }
        }
    } else {
        ForecastDetailsScreen(
            location = forecast.location,
            current = forecast.current,
            today = forecast.today,
            selected = state.selected,
            scoreResult = state.scoreResult,
            units = state.preferences.units,
            onHourSelected = model::selectHour,
            onBack = onBack,
        )
    }
}

@Composable
internal fun ForecastDetailsScreen(
    location: Location,
    current: ForecastBlock,
    today: ForecastDay,
    selected: ForecastBlock?,
    scoreResult: ScoreResult?,
    units: Units,
    onHourSelected: (ForecastBlock?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            DetailsTopBar(
                location = location,
                onBack = onBack,
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            CurrentConditionsHero(
                block = current,
                today = today.block,
                scoreResult = scoreResult,
                units = units,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.standard),
            )

            Spacer(modifier = Modifier.height(20.dp))

            val hours = remember(today.hours) {
                today.hours.toPersistentList()
            }
            HourlyForecastStrip(
                now = current,
                hours = hours,
                selected = selected,
                units = units,
                onHourSelected = onHourSelected,
            )

            Spacer(modifier = Modifier.height(20.dp))

            WeatherDetailsGrid(
                block = selected ?: current,
                units = units,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.standard),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun ForecastDetailsScreenPreview() {
    val now = Clock.System.now()
    val sunny = ForecastPreviewData.sunny(now)
    AppPreview {
        ForecastDetailsScreen(
            location = Location(43.6532, -79.3832, "London, ON"),
            current = sunny,
            today = ForecastPreviewData.createSunnyForecast().today,
            selected = sunny,
            scoreResult = ScoreResult.Yes,
            units = Units.Metric,
            onHourSelected = {},
            onBack = {},
        )
    }
}
