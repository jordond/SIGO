package app.sigot.onboarding.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.sigot.core.model.forecast.Location
import app.sigot.core.ui.components.Text
import dev.stateholder.extensions.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LocationScreen(model: LocationModel = koinViewModel()) {
    val state by model.collectAsState()

    LocationScreen(
        enableLocationUpdates = state.enableLocationUpdates,
        location = state.location,
        toggleLocationUpdates = model::toggleLocationUpdates,
        getCurrentLocation = model::getLocation,
    )
}

@Composable
internal fun LocationScreen(
    enableLocationUpdates: Boolean,
    location: Location?,
    toggleLocationUpdates: () -> Unit,
    getCurrentLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Text("Location Screen")
    }
}
