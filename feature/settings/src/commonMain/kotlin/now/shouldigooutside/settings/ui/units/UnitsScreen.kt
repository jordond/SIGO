package now.shouldigooutside.settings.ui.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.model.units.UnitPreset
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.onboarding_units
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Scaffold
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.units.UnitCardList
import now.shouldigooutside.settings.ui.components.SettingsTopBar
import now.shouldigooutside.settings.ui.components.SettingsTopBarNav
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UnitsScreen(
    onBack: () -> Unit,
    model: UnitsModel = koinViewModel(),
) {
    val state by model.collectAsState()

    UnitsScreen(
        units = state.units,
        update = model::update,
        onBack = onBack,
    )
}

@Composable
internal fun UnitsScreen(
    units: Units,
    update: (Units) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        containerColor = AppTheme.colors.surface,
        topBar = {
            SettingsTopBar(
                text = Res.string.onboarding_units,
                onBack = onBack,
                navType = SettingsTopBarNav.Back,
            )
        },
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                ).padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            UnitCardList(
                units = units,
                update = update,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
            )
        }
    }
}

@Composable
private fun ScreenPreview() {
    var units by remember { mutableStateOf(UnitPreset.SI.units) }
    UnitsScreen(
        units = units,
        update = { units = it },
    )
}

@Preview
@Composable
private fun UnitsScreenPreview() {
    AppPreview {
        ScreenPreview()
    }
}

@Preview
@Composable
private fun UnitsScreenDarkPreview() {
    AppPreview(isDarkTheme = true) {
        ScreenPreview()
    }
}
