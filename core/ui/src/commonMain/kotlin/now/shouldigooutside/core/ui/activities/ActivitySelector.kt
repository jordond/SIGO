package now.shouldigooutside.core.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.add
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.SelectionCard
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Plus
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData

@Composable
public fun ActivitySelector(
    selected: Activity,
    onSelected: (Activity) -> Unit,
    activities: PersistentList<Activity>,
    modifier: Modifier = Modifier,
    onAddCustom: () -> Unit = {},
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    canAdd: Boolean = true,
) {
    LaunchedEffect(selected) {
        val index = activities.indexOfFirst { it == selected }
        if (index != -1) {
            state.animateScrollToItem(index)
        }
    }

    LazyRow(
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        items(activities) { activity ->
            Item(
                icon = activity.rememberIcon(),
                label = activity.rememberStringResource().get(),
                selected = activity == selected,
                onClick = { onSelected(activity) },
                modifier = Modifier.animateItem(),
            )
        }

        if (canAdd) {
            item {
                Item(
                    icon = AppIcons.Lucide.Plus,
                    label = Res.string.add.get(),
                    selected = false,
                    onClick = onAddCustom,
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@Composable
private fun Item(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SelectionCard(
        isSelected = selected,
        onClick = onClick,
        modifier = modifier
            .size(100.dp)
            .aspectRatio(1f),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            modifier = Modifier.fillMaxSize(),
        ) {
            Icon(
                icon = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
            )

            Text(
                text = label,
                style = AppTheme.typography.body1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                autoSize = AppTheme.typography.body1.autoSize(),
            )
        }
    }
}

private class Params : PreviewParameterProvider<PersistentList<Activity>> {
    override val values: Sequence<PersistentList<Activity>> = sequenceOf(
        PreviewData.Activities.keys.toPersistentList(),
        persistentListOf(Activity.General),
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview(
    @PreviewParameter(Params::class) activities: PersistentList<Activity>,
) {
    AppPreview {
        ActivitySelector(
            selected = activities.first(),
            onSelected = {},
            activities = activities,
        )
    }
}
