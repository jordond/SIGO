package app.sigot.core.ui.units

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.sigot.core.model.units.UnitPreset
import app.sigot.core.resources.Res
import app.sigot.core.resources.unit_preset_imperial
import app.sigot.core.resources.unit_preset_metric
import app.sigot.core.resources.unit_preset_si
import app.sigot.core.ui.ktx.get
import org.jetbrains.compose.resources.StringResource

public fun UnitPreset.textRes(): StringResource =
    when (this) {
        UnitPreset.SI -> Res.string.unit_preset_si
        UnitPreset.Metric -> Res.string.unit_preset_metric
        UnitPreset.Imperial -> Res.string.unit_preset_imperial
    }

@Composable
public fun UnitPreset.rememberText(): String {
    val res = remember(this) { textRes() }
    return res.get()
}
