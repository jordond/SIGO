package app.sigot.onboarding.ui.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.sigot.core.model.units.Units
import app.sigot.core.ui.components.Text
import dev.stateholder.extensions.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UnitsScreen(model: UnitsModel = koinViewModel()) {
    val state by model.collectAsState()

    UnitsScreen(
        units = state.units,
        update = model::update,
    )
}

@Composable
internal fun UnitsScreen(
    units: Units,
    update: (Units) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Text("Units screen")
    }
}
