package now.shouldigooutside.forecast.ui.location

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.location_change
import now.shouldigooutside.core.resources.location_search_placeholder
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.textfield.TextField
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.forecast.ui.forecast.section.search.LocationResultsList
import now.shouldigooutside.forecast.ui.forecast.section.search.UseCurrentLocationButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LocationSearchBottomSheet(
    onBack: () -> Unit,
    model: LocationSearchModel = koinViewModel(),
) {
    val state by model.collectAsState()

    LocationSearchBottomSheet(
        query = state.searchQuery,
        results = state.searchResults,
        searching = state.searching,
        usingCurrentLocation = state.usingCurrentLocation,
        onQueryChange = model::searchLocation,
        onSelectLocation = { location ->
            model.selectLocation(location)
            onBack()
        },
        onUseCurrentLocation = {
            model.useCurrentLocation()
            onBack()
        },
    )
}

@Composable
internal fun LocationSearchBottomSheet(
    query: String,
    results: PersistentList<Location>,
    searching: Boolean,
    usingCurrentLocation: Boolean,
    onQueryChange: (String) -> Unit,
    onSelectLocation: (Location) -> Unit,
    onUseCurrentLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.standard)
            .padding(bottom = AppTheme.spacing.standard),
    ) {
        Text(
            text = Res.string.location_change.get(),
            style = AppTheme.typography.h3,
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            placeholder = {
                Text(
                    text = Res.string.location_search_placeholder.get(),
                    style = AppTheme.typography.body1,
                )
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        LocationResultsList(
            query = query,
            results = results,
            searching = searching,
            onSelectLocation = onSelectLocation,
        )

        Spacer(modifier = Modifier.height(8.dp))

        UseCurrentLocationButton(
            usingCurrentLocation = usingCurrentLocation,
            onClick = onUseCurrentLocation,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private data class LocationSearchState(
    val query: String,
    val results: PersistentList<Location>,
    val searching: Boolean,
    val usingCurrentLocation: Boolean,
)

private class LocationSearchParams : PreviewParameterProvider<LocationSearchState> {
    override val values: Sequence<LocationSearchState>
        get() = sequenceOf(
            LocationSearchState(
                query = "",
                results = persistentListOf(),
                searching = false,
                usingCurrentLocation = true,
            ),
            LocationSearchState(
                query = "New York",
                results = persistentListOf(),
                searching = true,
                usingCurrentLocation = true,
            ),
            LocationSearchState(
                query = "New York",
                results = persistentListOf(
                    Location(40.7128, -74.0060, "New York", "New York", "United States"),
                    Location(40.7282, -73.7949, "New York Mills", "New York", "United States"),
                ),
                searching = false,
                usingCurrentLocation = false,
            ),
        )
}

@Preview
@Composable
private fun LocationSearchBottomSheetPreview(
    @PreviewParameter(LocationSearchParams::class) state: LocationSearchState,
) {
    AppPreview {
        LocationSearchBottomSheet(
            query = state.query,
            results = state.results,
            searching = state.searching,
            usingCurrentLocation = state.usingCurrentLocation,
            onQueryChange = {},
            onSelectLocation = {},
            onUseCurrentLocation = {},
        )
    }
}
