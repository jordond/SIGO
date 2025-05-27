package app.sigot.core.ui.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.units.PrecipitationUnit
import app.sigot.core.model.units.PressureUnit
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.model.units.UnitPreset
import app.sigot.core.model.units.Units
import app.sigot.core.model.units.WindSpeedUnit
import app.sigot.core.model.units.units
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun UnitCardList(
    units: Units,
    update: (Units) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.width(IntrinsicSize.Min),
    ) {
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
