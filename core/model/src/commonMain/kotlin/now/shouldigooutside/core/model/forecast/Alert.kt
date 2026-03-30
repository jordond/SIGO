package now.shouldigooutside.core.model.forecast

import androidx.compose.runtime.Immutable

/**
 * An alert issued for the location.
 *
 * @property title The title of the alert.
 * @property description The description of the alert.
 */
@Immutable
public data class Alert(
    val title: String,
    val description: String,
)
