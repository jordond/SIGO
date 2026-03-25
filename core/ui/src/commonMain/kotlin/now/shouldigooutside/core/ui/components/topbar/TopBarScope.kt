package now.shouldigooutside.core.ui.components.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.back
import now.shouldigooutside.core.resources.close
import now.shouldigooutside.core.ui.LocalTextStyle
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.IconButton
import now.shouldigooutside.core.ui.components.IconButtonVariant
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.icons.lucide.ArrowLeft
import now.shouldigooutside.core.ui.icons.lucide.Lucide
import now.shouldigooutside.core.ui.icons.lucide.X
import now.shouldigooutside.core.ui.ktx.get

public class TopBarScope(
    delegate: RowScope,
) : RowScope by delegate {
    @Composable
    public fun NavIcon(
        icon: ImageVector,
        contentDescription: String?,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        IconButton(
            variant = IconButtonVariant.Outlined,
            modifier = modifier,
            onClick = onClick,
        ) {
            Icon(
                icon = icon,
                contentDescription = contentDescription,
            )
        }
    }

    @Composable
    public fun BackButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        NavIcon(
            icon = Lucide.ArrowLeft,
            contentDescription = Res.string.back.get(),
            onClick = onClick,
            modifier = modifier,
        )
    }

    @Composable
    public fun CloseButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        NavIcon(
            icon = Lucide.X,
            contentDescription = Res.string.close.get(),
            onClick = onClick,
            modifier = modifier,
        )
    }

    @Composable
    public fun Title(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = LocalTextStyle.current,
        autoSize: Boolean = true,
    ) {
        Text(
            text = text,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(unbounded = true),
            style = style,
            maxLines = 1,
            autoSize = if (!autoSize) null else style.autoSize(),
        )
    }

    @Composable
    public fun Action(
        icon: ImageVector,
        contentDescription: String?,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        variant: IconButtonVariant = IconButtonVariant.SecondaryElevated,
    ) {
        IconButton(
            modifier = modifier,
            variant = variant,
            onClick = onClick,
        ) {
            Icon(
                icon = icon,
                contentDescription = contentDescription,
            )
        }
    }
}
