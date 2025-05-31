package app.sigot.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

public interface Haptics {
    public val enabled: Boolean

    public fun vibrate(
        count: Int,
        type: HapticFeedbackType = HapticFeedbackType.LongPress,
    )

    public fun vibrate(type: HapticFeedbackType = HapticFeedbackType.LongPress) {
        vibrate(1, type)
    }

    public fun click() {
        vibrate(1, HapticFeedbackType.Confirm)
    }
}

@Composable
public fun Haptics.wrap(onClick: () -> Unit): () -> Unit =
    remember(onClick) {
        {
            click()
            onClick()
        }
    }

public fun Haptics?.vibrate(type: HapticFeedbackType = HapticFeedbackType.LongPress) {
    this?.vibrate(type)
}

public fun Haptics(
    enabled: Boolean,
    hapticFeedback: HapticFeedback,
): Haptics =
    object : Haptics {
        override val enabled: Boolean = enabled

        override fun vibrate(
            count: Int,
            type: HapticFeedbackType,
        ) {
            if (enabled) {
                repeat(count) {
                    hapticFeedback.performHapticFeedback(type)
                }
            }
        }
    }

public val LocalHaptics: ProvidableCompositionLocal<Haptics> = compositionLocalOf {
    object : Haptics {
        override val enabled: Boolean = false

        override fun vibrate(
            count: Int,
            type: HapticFeedbackType,
        ) {
            // No-op
        }
    }
}

@Composable
public fun rememberHaptics(enabled: Boolean): Haptics {
    val hapticFeedback = LocalHapticFeedback.current
    return remember(enabled) {
        Haptics(enabled, hapticFeedback)
    }
}
