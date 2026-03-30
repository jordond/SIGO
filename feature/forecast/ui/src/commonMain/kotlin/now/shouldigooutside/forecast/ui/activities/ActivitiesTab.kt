package now.shouldigooutside.forecast.ui.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentMap
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.activity_add_title
import now.shouldigooutside.core.resources.forecast_details_title
import now.shouldigooutside.core.resources.home_tab_activities
import now.shouldigooutside.core.ui.TabHeader
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ActivitiesTab(
    toSettings: () -> Unit,
    model: ActivitiesModel = koinViewModel(),
) {
    val state by model.collectAsState()

    ActivitiesTab(
        activities = state.activities,
        toSettings = toSettings,
    )
}

@Composable
internal fun ActivitiesTab(
    activities: PersistentMap<Activity, Preferences>,
    modifier: Modifier = Modifier,
    toSettings: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabHeader(
            title = Res.string.home_tab_activities,
            toSettings = toSettings,
        )

        Text("TODO: Activities")
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
public fun ActivityTabPreview() {
    AppPreview {
        ActivitiesTab(
            activities = PreviewData.Activities,
        )
    }
}
