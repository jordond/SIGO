package now.shouldigooutside.forecast.ui.forecast.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_details_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.TabHeader
import now.shouldigooutside.core.ui.components.LoadingBox
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.ForecastPreviewData
import now.shouldigooutside.forecast.ui.components.NoLocation
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ForecastDetailsScreen(
    onBack: () -> Unit,
    toSettings: () -> Unit,
    toLocationPicker: () -> Unit,
    model: ForecastDetailsModel = koinViewModel(),
) {
    val state by model.collectAsState()
    Crossfade(state.forecast to state.loadingForecast) { (forecast, loading) ->
        if (forecast == null && !loading) {
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize(),
            ) {
                TabHeader(
                    title = Res.string.forecast_details_title,
                    toSettings = toSettings,
                )
                NoLocation(
                    onSetLocation = toLocationPicker,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else if (forecast == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize(),
            ) {
                LoadingBox()
            }
        } else {
            ForecastDetailsScreen(
                forecast = forecast,
                selected = state.selected,
                selectedScore = state.selectedScore?.result,
                onSelected = model::select,
                onBack = onBack,
                toSettings = toSettings,
            )
        }
    }
}

@Composable
internal fun ForecastDetailsScreen(
    forecast: Forecast,
    selected: ForecastBlock?,
    selectedScore: ScoreResult?,
    onSelected: (ForecastBlock?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    toSettings: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(
            title = Res.string.forecast_details_title,
            toSettings = toSettings,
        )

        val locationAlpha by animateFloatAsState(
            targetValue = if (forecast.location.isDefaultName) 0f else 1f,
        )
        Text(
            text = forecast.location.name,
            style = AppTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = AppTheme.spacing.standard)
                .graphicsLayer {
                    alpha = locationAlpha
                },
        )

        Spacer(modifier = Modifier.height(8.dp))

        SelectedConditionsHero(
            block = selected ?: forecast.current,
            today = forecast.today.block,
            scoreResult = selectedScore,
            units = forecast.units,
            modifier = Modifier.padding(horizontal = AppTheme.spacing.standard),
        )

        Spacer(modifier = Modifier.height(20.dp))

        val hours = remember(forecast.today.hours) {
            forecast.today.hours
                .toPersistentList()
        }
        HourlyForecastStrip(
            now = forecast.current,
            tomorrow = forecast.tomorrow?.block,
            hours = hours,
            selected = selected,
            units = forecast.units,
            onSelected = onSelected,
        )

        Spacer(modifier = Modifier.height(20.dp))

        WeatherDetailsGrid(
            block = selected ?: forecast.current,
            units = forecast.units,
            modifier = Modifier.padding(horizontal = AppTheme.spacing.standard),
        )

        Spacer(Modifier.height(200.dp))
    }
}

private class Params : PreviewParameterProvider<Forecast> {
    override val values: Sequence<Forecast>
        get() = sequenceOf(
            ForecastPreviewData.createSunnyForecast(),
            ForecastPreviewData.createRainyForecast(),
            ForecastPreviewData.createColdForecast(),
        )
}

@PreviewLightDark
@Composable
private fun ForecastDetailsScreenPreview(
    @PreviewParameter(Params::class) forecast: Forecast,
) {
    val score = ForecastPreviewData.score(forecast)
    AppPreview {
        ForecastDetailsScreen(
            forecast = forecast,
            selected = forecast.hour(1),
            selectedScore = score.hours
                .getOrNull(1)
                ?.result,
            onSelected = {},
            onBack = {},
        )
    }
}

@Composable
public fun ForecastDetailsTabPreview() {
    ForecastDetailsScreenPreview(Params().values.first())
}

@PreviewLightDark
@Composable
private fun NoLocationPreview() {
    AppPreview {
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize(),
        ) {
            TabHeader(
                title = Res.string.forecast_details_title,
                toSettings = {},
            )
            NoLocation(
                onSetLocation = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
