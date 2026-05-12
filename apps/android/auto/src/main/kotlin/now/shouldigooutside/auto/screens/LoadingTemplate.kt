package now.shouldigooutside.auto.screens

import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template

internal fun loadingTemplate(): Template =
    PaneTemplate
        .Builder(Pane.Builder().setLoading(true).build())
        .setHeaderAction(Action.APP_ICON)
        .build()
