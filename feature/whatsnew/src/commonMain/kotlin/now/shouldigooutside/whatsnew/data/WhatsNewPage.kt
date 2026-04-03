package now.shouldigooutside.whatsnew.data

import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * A single page in the What's New pager. Tagged with [version] for tracking.
 */
public data class WhatsNewPage(
    val version: Int,
    val title: StringResource,
    val description: StringResource,
    val image: DrawableResource,
    val scale: ContentScale = ContentScale.Crop,
)
