package now.shouldigooutside.forecast.ui.activities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentMap
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.ui.components.Text
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ActivitiesTab(model: ActivitiesModel = koinViewModel()) {
    val state by model.collectAsState()

    ActivitiesTab(
        activities = state.activities,
    )
}

@Composable
public fun ActivitiesTab(
    activities: PersistentMap<Activity, Preferences>,
    modifier: Modifier = Modifier,
) {
    Text("TODO: Activities")
}
