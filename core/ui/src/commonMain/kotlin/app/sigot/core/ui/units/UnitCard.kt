package app.sigot.core.ui.units

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.units.BaseUnit
import app.sigot.core.model.units.UnitPreset
import app.sigot.core.model.units.units
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.SegmentedButton
import app.sigot.core.ui.components.SegmentedButtonDefaults
import app.sigot.core.ui.components.SegmentedButtonDefaults.itemShape
import app.sigot.core.ui.components.SingleChoiceSegmentedButtonRow
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.mappers.units.colors
import app.sigot.core.ui.mappers.units.rememberIcon
import app.sigot.core.ui.mappers.units.rememberTitle
import app.sigot.core.ui.mappers.units.rememberUnit
import app.sigot.core.ui.preview.AppPreview

@Composable
public fun <T : BaseUnit> UnitCard(
    items: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = selected.colors()
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = colors.container,
            contentColor = colors.contentColorFor(colors.container),
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
