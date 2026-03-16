package now.shouldigooutside.core.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import now.shouldigooutside.core.ui.icons.lucide.Share

@Suppress("ktlint:standard:enum-entry-name-case", "EnumEntryName")
public enum class PlatformIcon {
    iOS,

    Android,
}

public val LocalPlatformIcon: ProvidableCompositionLocal<PlatformIcon> =
    compositionLocalOf { error("LocalPlatformIcon not provided") }

public val AppIcons.Share: ImageVector
    @Composable get() = Lucide.Share
