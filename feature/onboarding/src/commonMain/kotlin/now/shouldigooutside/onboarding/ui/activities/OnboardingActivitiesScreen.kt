package now.shouldigooutside.onboarding.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.onboarding_activities
import now.shouldigooutside.core.resources.onboarding_activities_pro_tip
import now.shouldigooutside.core.resources.onboarding_activities_pro_tip_text
import now.shouldigooutside.core.resources.onboarding_activities_subtext
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.activities.key
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Info
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.onboarding.ui.OnboardingScreenPreview
import now.shouldigooutside.onboarding.ui.navigation.OnboardingDestination
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OnboardingActivitiesScreen(model: OnboardingActivitiesModel = koinViewModel()) {
    val state by model.collectAsState()
    OnboardingActivitiesScreen(
        selectedActivities = state.selectedActivities,
        activities = state.availableActivities,
        onToggleActivity = model::toggleActivity,
    )
}

@Composable
internal fun OnboardingActivitiesScreen(
    selectedActivities: PersistentSet<Activity>,
    activities: PersistentList<Activity>,
    onToggleActivity: (Activity) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier.padding(bottom = 4.dp),
            ) {
                Text(
                    text = Res.string.onboarding_activities,
                    style = AppTheme.typography.header,
                    autoSize = AppTheme.typography.header.autoSize(),
                    maxLines = 1,
                )

                Text(
                    text = Res.string.onboarding_activities_subtext,
                    modifier = Modifier.padding(start = 8.dp),
                    style = AppTheme.typography.body1,
                )
            }
        }

        items(
            items = activities,
            key = { it.key() },
        ) { activity ->
            OnboardingActivityCard(
                activity = activity,
                selected = activity in selectedActivities,
                onClick = { onToggleActivity(activity) },
                modifier = Modifier.aspectRatio(1f),
            )
        }

        item(
            key = "pro_tip",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Card(
                colors = CardDefaults.disclaimerColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
                    modifier = Modifier.padding(12.dp),
                ) {
                    Icon(
                        icon = AppIcons.Lucide.Info,
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = Res.string.onboarding_activities_pro_tip.get(),
                            style = AppTheme.typography.h3,
                        )

                        Text(
                            text = Res.string.onboarding_activities_pro_tip_text.get(),
                            style = AppTheme.typography.body3,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ActivitiesScreenPreview() {
    OnboardingScreenPreview(OnboardingDestination.Activities)
}
