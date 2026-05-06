package now.shouldigooutside.core.ui

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.runtime.Composable

@Composable
public actual fun ReportFullyDrawnWhen(predicate: () -> Boolean) {
    ReportDrawnWhen(predicate)
}
