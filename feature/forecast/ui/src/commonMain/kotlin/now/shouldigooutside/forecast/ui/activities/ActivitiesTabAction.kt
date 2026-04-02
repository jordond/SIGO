package now.shouldigooutside.forecast.ui.activities

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.preferences.Activity

@Immutable
internal sealed interface ActivitiesTabAction {
    data class ChangePeriod(
        val period: ForecastPeriod,
    ) : ActivitiesTabAction

    data class ActivityClick(
        val activity: Activity,
    ) : ActivitiesTabAction

    data object ToSettings : ActivitiesTabAction

    data object ToAddActivity : ActivitiesTabAction

    data object ToLocationPicker : ActivitiesTabAction
}
