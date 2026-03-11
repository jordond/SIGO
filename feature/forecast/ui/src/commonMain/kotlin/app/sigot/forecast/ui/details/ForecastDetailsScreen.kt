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
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.resources.Res
import app.sigot.core.resources.something_went_wrong
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.ForecastPreviewData
import app.sigot.core.ui.preview.PreviewData.location
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.toPersistentList
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ForecastDetailsScreen(
    onBack: () -> Unit,
    model: ForecastDetailsModel = koinViewModel(),
) {
    val state by model.collectAsState()
    val data = state.data

    if (data == null) {
        Card {
            Column(
                modifier = Modifier.padding(AppTheme.spacing.standard),
            ) {
                Text(text = Res.string.something_went_wrong)
            }
        }
    } else {
        ForecastDetailsScreen(
            data = data,
            selected = state.selected,
            onHourSelected = model::selectHour,
            onBack = onBack,
        )
    }
}

@Composable
internal fun ForecastDetailsScreen(
    data: ForecastData,
    selected: ForecastBlock?,
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

            SelectedConditionsHero(
                block = selected ?: data.forecast.current,
                today = data.forecast.today.block,
                scoreResult = data.score.current.result,
                units = data.forecast.units,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.standard),
            )

            Spacer(modifier = Modifier.height(20.dp))

            val hours = remember(data.forecast.today.hours) {
                data.forecast.today.hours
                    .toPersistentList()
            }
            HourlyForecastStrip(
                now = data.forecast.current,
                hours = hours,
                selected = selected,
                units = data.forecast.units,
                onHourSelected = onHourSelected,
            )

            Spacer(modifier = Modifier.height(20.dp))

            WeatherDetailsGrid(
                block = selected ?: data.forecast.current,
                units = data.forecast.units,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.standard),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun ForecastDetailsScreenPreview() {
    val data = ForecastPreviewData.forecastData(ForecastPreviewData.createSunnyForecast())
    AppPreview {
        ForecastDetailsScreen(
            data = data,
            selected = data.forecast.current,
            onHourSelected = {},
            onBack = {},
        )
    }
}
