package app.sigot.forecast.ui.section.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.sigot.core.model.location.Location
import app.sigot.core.resources.Res
import app.sigot.core.resources.location_no_results
import app.sigot.core.resources.location_searching
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.progressindicators.LinearProgressIndicator
import app.sigot.core.ui.icons.lucide.Lucide
import app.sigot.core.ui.icons.lucide.MapPinXInside
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun LocationResultsList(
    query: String,
    results: PersistentList<Location>,
    searching: Boolean,
    onSelectLocation: (Location) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp, max = 300.dp),
    ) {
        when {
            searching -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Res.string.location_searching.get(),
                        style = AppTheme.typography.body2,
                        color = AppTheme.colors.textSecondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }

            results.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(results, key = { "${it.latitude},${it.longitude}" }) { location ->
                        LocationResultCard(
                            location = location,
                            onClick = { onSelectLocation(location) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            query.isNotBlank() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            icon = Lucide.MapPinXInside,
                            modifier = Modifier.size(24.dp),
                            tint = AppTheme.colors.textSecondary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Res.string.location_no_results.get(),
                            style = AppTheme.typography.body1,
                            color = AppTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

private data class LocationResultsListState(
    val query: String,
    val results: PersistentList<Location>,
    val searching: Boolean,
)

private class ResultsParams : PreviewParameterProvider<LocationResultsListState> {
    override val values: Sequence<LocationResultsListState>
        get() = sequenceOf(
            LocationResultsListState(
                query = "New York",
                results = persistentListOf(),
                searching = true,
            ),
            LocationResultsListState(
                query = "New York",
                results = persistentListOf(
                    Location(40.7128, -74.0060, "New York"),
                    Location(40.7282, -73.7949, "New York Mills"),
                    Location(42.6866, -73.8269, "New York State Capitol"),
                ),
                searching = false,
            ),
            LocationResultsListState(
                query = "xyzzy",
                results = persistentListOf(),
                searching = false,
            ),
            LocationResultsListState(
                query = "",
                results = persistentListOf(),
                searching = false,
            ),
        )
}

@Preview
@Composable
private fun LocationResultsListPreview(
    @PreviewParameter(ResultsParams::class) state: LocationResultsListState,
) {
    AppPreview {
        LocationResultsList(
            query = state.query,
            results = state.results,
            searching = state.searching,
            onSelectLocation = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
