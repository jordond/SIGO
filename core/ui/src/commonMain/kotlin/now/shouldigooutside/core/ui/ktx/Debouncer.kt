package now.shouldigooutside.core.ui.ktx

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import now.shouldigooutside.core.platform.currentTimeMillis
import kotlin.time.Clock

public class Debouncer<T>(
    private val millis: Long,
    private val action: (T) -> Unit,
) {
    private var lastInvokeTime = 0L

    public operator fun invoke(param: T) {
        val currentTime = currentTimeMillis()
        if (currentTime - lastInvokeTime >= millis) {
            action(param)
            lastInvokeTime = currentTime
        }
    }
}

public fun <T> ((T) -> Unit).debounced(millis: Long = 300L): Debouncer<T> = Debouncer(millis, this)

public fun (() -> Unit).debounced(millis: Long = 300L): Debouncer<Unit> = Debouncer(millis) { this() }

@Composable
public fun <T> ((T) -> Unit).rememberDebounced(millis: Long = 300L): Debouncer<T> =
    remember(this, millis) {
        this.debounced(millis)
    }

@Composable
public fun <T> rememberDebounced(
    millis: Long = 300L,
    block: (T) -> Unit,
): Debouncer<T> =
    remember(block, millis) {
        block.debounced(millis)
    }

public inline fun Modifier.debounceClickable(
    debounce: Long = 400,
    crossinline onClick: () -> Unit,
): Modifier =
    composed {
        var lastClickTime by remember { mutableStateOf(0L) }
        clickable {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            if ((currentTime - lastClickTime) < debounce) return@clickable
            lastClickTime = currentTime
            onClick()
        }
    }
