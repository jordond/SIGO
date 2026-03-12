package app.sigot.forecast.ui.section.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.sigot.core.model.location.Location
import app.sigot.core.resources.Res
import app.sigot.core.resources.location_change
import app.sigot.core.resources.location_search_placeholder
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.ModalBottomSheet
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.textfield.TextField
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun LocationSearchSheet(
    isVisible: Boolean,
    usingCurrentLocation: Boolean,
    query: String,
    results: PersistentList<Location>,
    searching: Boolean,
    onQueryChange: (String) -> Unit,
    onSelectLocation: (Location) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        isVisible = isVisible,
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
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
}

private data class LocationSearchSheetState(
    val query: String,
    val results: PersistentList<Location>,
    val searching: Boolean,
    val usingCurrentLocation: Boolean,
)

private class SheetParams : PreviewParameterProvider<LocationSearchSheetState> {
    override val values: Sequence<LocationSearchSheetState>
        get() = sequenceOf(
            LocationSearchSheetState(
                query = "",
                results = persistentListOf(),
                searching = false,
                usingCurrentLocation = true,
            ),
            LocationSearchSheetState(
                query = "New York",
                results = persistentListOf(),
                searching = true,
                usingCurrentLocation = true,
            ),
            LocationSearchSheetState(
                query = "New York",
                results = persistentListOf(
                    Location(40.7128, -74.0060, "New York", "New York", "United States"),
                    Location(40.7282, -73.7949, "New York Mills", "New York", "United States"),
                ),
                searching = false,
                usingCurrentLocation = false,
            ),
            LocationSearchSheetState(
                query = "xyzzy",
                results = persistentListOf(),
                searching = false,
                usingCurrentLocation = true,
            ),
        )
}

@Preview
@Composable
private fun LocationSearchSheetPreview(
    @PreviewParameter(SheetParams::class) state: LocationSearchSheetState,
) {
    AppPreview {
        LocationSearchSheet(
            isVisible = true,
            usingCurrentLocation = state.usingCurrentLocation,
            query = state.query,
            results = state.results,
            searching = state.searching,
            onQueryChange = {},
            onSelectLocation = {},
            onUseCurrentLocation = {},
            onDismiss = {},
        )
    }
}
