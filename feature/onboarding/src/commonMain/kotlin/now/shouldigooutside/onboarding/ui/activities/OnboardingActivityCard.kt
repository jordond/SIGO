package now.shouldigooutside.onboarding.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.activities.colors
import now.shouldigooutside.core.ui.activities.rememberDisplayName
import now.shouldigooutside.core.ui.activities.rememberIcon
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.brutalBorder
import now.shouldigooutside.core.ui.components.card.SelectionCard
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun OnboardingActivityCard(
    activity: Activity,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayName = activity.rememberDisplayName()
    val icon = activity.rememberIcon()
    val colors = activity.colors()

    SelectionCard(
        isSelected = selected,
        onClick = onClick,
        selectedColor = colors.bright,
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = colors.container,
                        shape = AppTheme.shapes.extraSmall,
                    ).brutalBorder(shape = AppTheme.shapes.extraSmall),
            ) {
                Icon(
                    icon = icon,
                    contentDescription = displayName,
                    tint = colors.containerContent,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(28.dp),
                )
            }

            Text(
                text = displayName,
                style = AppTheme.typography.body2,
                autoSize = AppTheme.typography.body2.autoSize(),
                maxLines = 1,
            )
        }
    }
}

@Preview(name = "Selected")
@Composable
private fun SelectedPreview() {
    AppPreview {
        OnboardingActivityCard(
            activity = Activity.Running,
            selected = true,
            onClick = {},
            modifier = Modifier.size(100.dp),
        )
    }
}

@Preview(name = "Unselected")
@Composable
private fun UnselectedPreview() {
    AppPreview {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Activity.all.filterNot { it is Activity.General }.forEach { activity ->
                OnboardingActivityCard(
                    activity = activity,
                    selected = false,
                    onClick = {},
                    modifier = Modifier.size(100.dp),
                )
            }
        }
    }
}
