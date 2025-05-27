package app.sigot.core.ui.units

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.units.UnitPreset
import app.sigot.core.model.units.Units
import app.sigot.core.model.units.units
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.brutal
import app.sigot.core.ui.components.SegmentedButton
import app.sigot.core.ui.components.SegmentedButtonDefaults
import app.sigot.core.ui.components.SegmentedButtonDefaults.itemShape
import app.sigot.core.ui.components.SingleChoiceSegmentedButtonRow
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.mappers.units.rememberText
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun UnitPresetCard(
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

@Preview
@Composable
private fun UnitCardListPreview() {
    var units by remember { mutableStateOf(UnitPreset.SI.units) }
    AppPreview {
        Box(Modifier.padding(16.dp)) {
            UnitCardList(
                units = units,
                update = { units = it },
            )
        }
    }
}
