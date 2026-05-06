package now.shouldigooutside.core.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

public actual fun Modifier.uiAutomatorTag(tag: String): Modifier =
    this
        .semantics { testTagsAsResourceId = true }
        .testTag(tag)
