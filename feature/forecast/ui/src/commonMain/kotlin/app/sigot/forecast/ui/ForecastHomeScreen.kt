package app.sigot.forecast.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.sigot.core.model.location.Location
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ForecastHomeScreen(model: ForecastHomeModel = koinViewModel()) {
    ForecastHomeScreen(
        location = null,
    )
}

@Composable
internal fun ForecastHomeScreen(
    location: Location?,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            Text("Forecast home")
        }
    }
}
