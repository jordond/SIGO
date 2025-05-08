package app.sigot.core.ui.components.foundation

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.unit.Dp
import app.sigot.core.ui.components.LocalContentColor
import app.sigot.core.ui.components.foundation.RippleDefaults.RippleAlpha

@Stable
public fun ripple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified,
): IndicationNodeFactory {
    return if (radius == Dp.Unspecified && color == Color.Unspecified) {
        if (bounded) return DefaultBoundedRipple else DefaultUnboundedRipple
    } else {
        RippleNodeFactory(bounded, radius, color)
    }
}

@Stable
public fun ripple(
    color: ColorProducer,
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
): IndicationNodeFactory = RippleNodeFactory(bounded, radius, color)

/** Default values used by [ripple]. */
public object RippleDefaults {
    /**
     * Represents the default [RippleAlpha] that will be used for a ripple to indicate different
     * states.
     */
    public val RippleAlpha: RippleAlpha
        get() = RippleAlpha(
            pressedAlpha = StateTokens.PressedStateLayerOpacity,
            focusedAlpha = StateTokens.FocusStateLayerOpacity,
            draggedAlpha = StateTokens.DraggedStateLayerOpacity,
            hoveredAlpha = StateTokens.HoverStateLayerOpacity,
        )
}

public val LocalRippleConfiguration: ProvidableCompositionLocal<RippleConfiguration?> =
    compositionLocalOf {
        RippleConfiguration()
    }

@Immutable
public class RippleConfiguration(
    public val color: Color = Color.Unspecified,
    public val rippleAlpha: RippleAlpha? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RippleConfiguration) return false

        if (color != other.color) return false
        if (rippleAlpha != other.rippleAlpha) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + (rippleAlpha?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "RippleConfiguration(color=$color, rippleAlpha=$rippleAlpha)"
}

@Stable
private class RippleNodeFactory
    private constructor(
        private val bounded: Boolean,
        private val radius: Dp,
        private val colorProducer: ColorProducer?,
        private val color: Color,
    ) : IndicationNodeFactory {
        constructor(
            bounded: Boolean,
            radius: Dp,
            colorProducer: ColorProducer,
        ) : this(bounded, radius, colorProducer, Color.Unspecified)

        constructor(bounded: Boolean, radius: Dp, color: Color) : this(bounded, radius, null, color)

        override fun create(interactionSource: InteractionSource): DelegatableNode {
            val colorProducer = colorProducer ?: ColorProducer { color }
            return DelegatingThemeAwareRippleNode(interactionSource, bounded, radius, colorProducer)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is RippleNodeFactory) return false

            if (bounded != other.bounded) return false
            if (radius != other.radius) return false
            if (colorProducer != other.colorProducer) return false
            return color == other.color
        }

        override fun hashCode(): Int {
            var result = bounded.hashCode()
            result = 31 * result + radius.hashCode()
            result = 31 * result + colorProducer.hashCode()
            result = 31 * result + color.hashCode()
            return result
        }
    }

private class DelegatingThemeAwareRippleNode(
    private val interactionSource: InteractionSource,
    private val bounded: Boolean,
    private val radius: Dp,
    private val color: ColorProducer,
) : DelegatingNode(),
    CompositionLocalConsumerModifierNode,
    ObserverModifierNode {
    private var rippleNode: DelegatableNode? = null

    override fun onAttach() {
        updateConfiguration()
    }

    override fun onObservedReadsChanged() {
        updateConfiguration()
    }

    /**
     * Handles [LocalRippleConfiguration] changing between null / non-null. Changes to
     * [RippleConfiguration.color] and [RippleConfiguration.rippleAlpha] are handled as part of the
     * ripple definition.
     */
    private fun updateConfiguration() {
        observeReads {
            val configuration = currentValueOf(LocalRippleConfiguration)
            if (configuration == null) {
                removeRipple()
            } else {
                if (rippleNode == null) attachNewRipple()
            }
        }
    }

    private fun attachNewRipple() {
        val calculateColor =
            ColorProducer {
                val userDefinedColor = color()
                if (userDefinedColor.isSpecified) {
                    userDefinedColor
                } else {
                    // If this is null, the ripple will be removed, so this should always be non-null in
                    // normal use
                    val rippleConfiguration = currentValueOf(LocalRippleConfiguration)
                    if (rippleConfiguration?.color?.isSpecified == true) {
                        rippleConfiguration.color
                    } else {
                        currentValueOf(LocalContentColor)
                    }
                }
            }

        val calculateRippleAlpha = {
            // If this is null, the ripple will be removed, so this should always be non-null in
            // normal use
            val rippleConfiguration = currentValueOf(LocalRippleConfiguration)
            rippleConfiguration?.rippleAlpha ?: RippleDefaults.RippleAlpha
        }

        rippleNode =
            delegate(
                createRippleModifierNode(
                    interactionSource = interactionSource,
                    bounded = bounded,
                    radius = radius,
                    color = calculateColor,
                    rippleAlpha = calculateRippleAlpha,
                ),
            )
    }

    private fun removeRipple() {
        rippleNode?.let { undelegate(it) }
        rippleNode = null
    }
}

@Suppress("ConstPropertyName")
private object StateTokens {
    const val DraggedStateLayerOpacity = 0.16f
    const val FocusStateLayerOpacity = 0.1f
    const val HoverStateLayerOpacity = 0.08f
    const val PressedStateLayerOpacity = 0.1f
}

private val DefaultBoundedRipple =
    RippleNodeFactory(bounded = true, radius = Dp.Unspecified, color = Color.Unspecified)

private val DefaultUnboundedRipple =
    RippleNodeFactory(bounded = false, radius = Dp.Unspecified, color = Color.Unspecified)
