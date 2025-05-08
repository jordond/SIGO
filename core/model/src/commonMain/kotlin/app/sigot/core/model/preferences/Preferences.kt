package app.sigot.core.model.preferences

import app.sigot.core.model.preferences.units.Units

public data class Preferences(
    public val units: Units = Units(),
)
