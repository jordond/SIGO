package now.shouldigooutside.core.ui.icons

import androidx.compose.material.icons.Icons
import now.shouldigooutside.core.ui.icons.lucide.Lucide
import now.shouldigooutside.core.ui.icons.phosphor.Phosphor
import now.shouldigooutside.core.ui.icons.tabler.Tabler

public object AppIcons {
    @Suppress("RemoveRedundantQualifierName")
    public val Lucide: Lucide = now.shouldigooutside.core.ui.icons.lucide.Lucide

    @Suppress("RemoveRedundantQualifierName")
    public val Tabler: Tabler = now.shouldigooutside.core.ui.icons.tabler.Tabler

    @Suppress("RemoveRedundantQualifierName")
    public val Phosphor: Phosphor = now.shouldigooutside.core.ui.icons.phosphor.Phosphor
}

@Suppress("UnusedReceiverParameter")
public val Icons.App: AppIcons
    get() = AppIcons
