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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.score.ScoreResult
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.ForecastPreviewData
import app.sigot.core.ui.preview.PreviewData.location
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.something_went_wrong
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
            selectedScore = state.selectedScore?.result,
            onSelected = model::select,
            onBack = onBack,
        )
    }
}

@Composable
internal fun ForecastDetailsScreen(
    data: ForecastData,
    selected: ForecastBlock?,
    selectedScore: ScoreResult?,
    onSelected: (ForecastBlock?) -> Unit,
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
                scoreResult = selectedScore,
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
                tomorrow = data.forecast.tomorrow?.block,
                hours = hours,
                selected = selected,
                units = data.forecast.units,
                onSelected = onSelected,
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

private class Params : PreviewParameterProvider<ForecastData> {
    override val values: Sequence<ForecastData>
        get() = sequenceOf(
            ForecastPreviewData.forecastData(ForecastPreviewData.createSunnyForecast()),
            ForecastPreviewData.forecastData(ForecastPreviewData.createRainyForecast()),
            ForecastPreviewData.forecastData(ForecastPreviewData.createColdForecast()),
        )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun ForecastDetailsScreenPreview(
    @PreviewParameter(Params::class) data: ForecastData,
) {
    AppPreview {
        ForecastDetailsScreen(
            data = data,
            selected = data.forecast.hour(1),
            selectedScore = data.score.hours
                .getOrNull(1)
                ?.result,
            onSelected = {},
            onBack = {},
        )
    }
}
