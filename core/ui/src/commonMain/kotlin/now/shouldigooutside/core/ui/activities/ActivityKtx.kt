package now.shouldigooutside.core.ui.activities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.activity_title_custom
import now.shouldigooutside.core.resources.activity_title_cycling
import now.shouldigooutside.core.resources.activity_title_general
import now.shouldigooutside.core.resources.activity_title_hiking
import now.shouldigooutside.core.resources.activity_title_running
import now.shouldigooutside.core.resources.activity_title_swimming
import now.shouldigooutside.core.resources.activity_title_walking
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.CloudSun
import now.shouldigooutside.core.ui.icons.lucide.SlidersVertical
import now.shouldigooutside.core.ui.icons.phosphor.Bicycle
import now.shouldigooutside.core.ui.icons.phosphor.Hike
import now.shouldigooutside.core.ui.icons.tabler.Run
import now.shouldigooutside.core.ui.icons.tabler.Swim
import now.shouldigooutside.core.ui.icons.tabler.Walk
import org.jetbrains.compose.resources.StringResource

@Composable
public fun Activity.rememberStringResource(): StringResource =
    remember(this) {
        when (this) {
            is Activity.Custom -> Res.string.activity_title_custom
            is Activity.Cycling -> Res.string.activity_title_cycling
            is Activity.General -> Res.string.activity_title_general
            is Activity.Hiking -> Res.string.activity_title_hiking
            is Activity.Running -> Res.string.activity_title_running
            is Activity.Swimming -> Res.string.activity_title_swimming
            is Activity.Walking -> Res.string.activity_title_walking
        }
    }

@Composable
public fun Activity.rememberIcon(): ImageVector =
    remember(this) {
        when (this) {
            is Activity.Custom -> AppIcons.Lucide.SlidersVertical
            is Activity.Cycling -> AppIcons.Phosphor.Bicycle
            is Activity.General -> AppIcons.Lucide.CloudSun
            is Activity.Hiking -> AppIcons.Phosphor.Hike
            is Activity.Running -> AppIcons.Tabler.Run
            is Activity.Swimming -> AppIcons.Tabler.Swim
            is Activity.Walking -> AppIcons.Tabler.Walk
        }
    }
