
package now.shouldigooutside.core.ui.foundation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable

public val WindowInsets.Companion.systemBarsForVisualComponents: WindowInsets
    @Composable
    get() = systemBars
