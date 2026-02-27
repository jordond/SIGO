package app.sigot.onboarding.ui.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.units.Units
import app.sigot.core.resources.Res
import app.sigot.core.resources.onboarding_units
import app.sigot.core.resources.onboarding_units_subtext
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.autoSize
import app.sigot.core.ui.units.UnitCardList
import app.sigot.onboarding.ui.OnboardingScreenPreview
import app.sigot.onboarding.ui.navigation.OnboardingDestination
import dev.stateholder.extensions.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OnboardingUnitsScreen(model: OnboardingUnitsModel = koinViewModel()) {
    val state by model.collectAsState()

    OnboardingUnitsScreen(
        units = state.units,
        update = model::update,
    )
}

@Composable
internal fun OnboardingUnitsScreen(
    units: Units,
    update: (Units) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text = Res.string.onboarding_units,
                    style = AppTheme.typography.header,
                    autoSize = AppTheme.typography.header.autoSize(),
                    maxLines = 1,
                )

                Text(
                    text = Res.string.onboarding_units_subtext,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            UnitCardList(
                units = units,
                update = update,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun UnitsScreenPreview() {
    OnboardingScreenPreview(OnboardingDestination.Units)
}

@Preview
@Composable
private fun UnitsScreenDarkPreview() {
    OnboardingScreenPreview(OnboardingDestination.Units, isDarkTheme = true)
}
