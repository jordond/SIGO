package app.sigot.forecast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import app.sigot.core.resources.location_searching
import app.sigot.core.resources.location_use_current
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.HorizontalDivider
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

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onUseCurrentLocation,
                variant = ButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = Res.string.location_use_current.get(),
                    style = AppTheme.typography.button,
                )
            }

            if (searching && results.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Res.string.location_searching.get(),
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.textSecondary,
                )
            }

            if (results.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(results) { location ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectLocation(location) }
                                .padding(vertical = 12.dp),
                        ) {
                            Text(
                                text = location.name,
                                style = AppTheme.typography.body1,
                            )
                            Text(
                                text = "${location.roundedLatitude}, ${location.roundedLongitude}",
                                style = AppTheme.typography.body2,
                                color = AppTheme.colors.textSecondary,
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

private data class LocationSearchSheetState(
    val query: String,
    val results: PersistentList<Location>,
    val searching: Boolean,
)

private class Params : PreviewParameterProvider<LocationSearchSheetState> {
    override val values: Sequence<LocationSearchSheetState>
        get() = sequenceOf(
            LocationSearchSheetState(
                query = "",
                results = persistentListOf(),
                searching = false,
            ),
            LocationSearchSheetState(
                query = "New York",
                results = persistentListOf(),
                searching = true,
            ),
            LocationSearchSheetState(
                query = "New York",
                results = persistentListOf(
                    Location(40.7128, -74.0060, "New York"),
                    Location(40.7282, -73.7949, "New York Mills"),
                ),
                searching = false,
            ),
        )
}

@Preview
@Composable
private fun LocationSearchSheetPreview(
    @PreviewParameter(Params::class) state: LocationSearchSheetState,
) {
    AppPreview {
        LocationSearchSheet(
            isVisible = true,
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
