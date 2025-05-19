package app.sigot.onboarding.ui.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.units.BaseUnit
import app.sigot.core.model.units.PrecipitationUnit
import app.sigot.core.model.units.PressureUnit
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.model.units.UnitPreset
import app.sigot.core.model.units.Units
import app.sigot.core.model.units.WindSpeedUnit
import app.sigot.core.model.units.units
import app.sigot.core.resources.Res
import app.sigot.core.resources.onboarding_units
import app.sigot.core.resources.onboarding_units_subtext
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.brutal
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.SegmentedButton
import app.sigot.core.ui.components.SegmentedButtonDefaults
import app.sigot.core.ui.components.SegmentedButtonDefaults.itemShape
import app.sigot.core.ui.components.SingleChoiceSegmentedButtonRow
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.autoSize
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.units.colors
import app.sigot.core.ui.units.rememberIcon
import app.sigot.core.ui.units.rememberText
import app.sigot.core.ui.units.rememberTitle
import app.sigot.core.ui.units.rememberUnit
import app.sigot.onboarding.ui.OnboardingScreenPreview
import app.sigot.onboarding.ui.navigation.OnboardingDestination
import dev.stateholder.extensions.collectAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
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
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.verticalScroll(rememberScrollState()),
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

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    UnitPresetCard(
                        units = units,
                        onSelect = { preset -> update(preset.units) },
                    )
                }

                UnitCard(
                    items = remember { TemperatureUnit.entries.toList() },
                    selected = units.temperature,
                    onSelect = { value -> update(units.copy(temperature = value)) },
                )

                UnitCard(
                    items = remember { WindSpeedUnit.entries.toList() },
                    selected = units.windSpeed,
                    onSelect = { value -> update(units.copy(windSpeed = value)) },
                )

                UnitCard(
                    items = remember { PrecipitationUnit.entries.toList() },
                    selected = units.precipitation,
                    onSelect = { value -> update(units.copy(precipitation = value)) },
                )

                UnitCard(
                    items = remember { PressureUnit.entries.toList() },
                    selected = units.pressure,
                    onSelect = { value -> update(units.copy(pressure = value)) },
                )
            }
        }
    }
}

@Composable
private fun UnitPresetCard(
    units: Units,
    onSelect: (UnitPreset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = remember(units) {
        when (units) {
            UnitPreset.SI.units -> UnitPreset.SI
            UnitPreset.Metric.units -> UnitPreset.Metric
            UnitPreset.Imperial.units -> UnitPreset.Imperial
            else -> null
        }
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.brutal.yellow.low,
            contentColor = AppTheme.colors.brutal.yellow.onLow,
        ),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(75.dp)
                .padding(vertical = 8.dp, horizontal = 16.dp),
        ) {
            SingleChoiceSegmentedButtonRow {
                UnitPreset.entries.forEachIndexed { index, preset ->
                    SegmentedButton(
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = AppTheme.colors.brutal.yellow.bright,
                            activeContentColor = AppTheme.colors.brutal.yellow.onBright,
                            inactiveContainerColor = AppTheme.colors.brutal.yellow.lowest,
                            inactiveContentColor = AppTheme.colors.brutal.yellow.onLowest,
                        ),
                        shape = itemShape(
                            index = index,
                            count = UnitPreset.entries.size,
                        ),
                        onClick = { onSelect(preset) },
                        selected = selected == preset,
                        label = { Text(preset.rememberText()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun <T : BaseUnit> UnitCard(
    items: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = selected.colors()
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = colors.containerColor,
            contentColor = colors.contentColorFor(colors.containerColor),
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(75.dp)
                .padding(8.dp),
        ) {
            Icon(
                icon = selected.rememberIcon(),
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.width(8.dp))

            Text(
                text = selected.rememberTitle(),
                style = AppTheme.typography.h4,
                modifier = Modifier.weight(1f),
            )

            SingleChoiceSegmentedButtonRow {
                items.forEachIndexed { index, unit ->
                    SegmentedButton(
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = colors.bright,
                            activeContentColor = colors.onBright,
                            inactiveContainerColor = colors.lowest,
                            inactiveContentColor = colors.onLowest,
                        ),
                        shape = itemShape(
                            index = index,
                            count = items.size,
                        ),
                        onClick = { onSelect(unit) },
                        selected = selected == unit,
                        label = { Text(unit.rememberUnit()) },
                    )
                }
            }
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
