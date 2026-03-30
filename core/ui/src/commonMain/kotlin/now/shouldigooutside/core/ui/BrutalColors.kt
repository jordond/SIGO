package now.shouldigooutside.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import now.shouldigooutside.core.ui.components.SliderDefaults
import now.shouldigooutside.core.ui.components.SwitchColors
import now.shouldigooutside.core.ui.components.SwitchDefaults
import now.shouldigooutside.core.ui.components.card.CardColors
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.foundation.slider.SliderColors

@Stable
public data class BrutalColors(
    val bright: Color,
    val onBright: Color,
    val high: Color,
    val onHigh: Color,
    val normal: Color,
    val onNormal: Color,
    val low: Color,
    val onLow: Color,
    val lowest: Color,
    val onLowest: Color,
) {
    public val container: Color = normal
    public val containerContent: Color = contentColorFor(container)

    public fun contentColorFor(color: Color): Color =
        when (color) {
            bright -> onBright
            high -> onHigh
            normal -> onNormal
            low -> onLow
            lowest -> onLowest
            else -> onNormal
        }
}

@Composable
public fun BrutalColors.cardColors(): CardColors =
    CardDefaults.cardColors(
        containerColor = container,
        contentColor = contentColorFor(container),
    )

@Composable
public fun BrutalColors.sliderColors(): SliderColors =
    SliderDefaults.colors(
        activeTrackColor = bright,
        inactiveTrackColor = low,
    )

@Composable
public fun BrutalColors.switchColors(): SwitchColors =
    SwitchDefaults.colors(
        checkedTrackColor = bright,
        uncheckedTrackColor = lowest,
    )

@Suppress("UnusedReceiverParameter")
public val Colors.brutal: BrutalColor
    @Composable get() = BrutalColor

public object BrutalColor {
    public val blue: BrutalColors = BrutalColors(
        bright = Color(0xFF7DF9FF),
        onBright = Color.Black,
        high = Color(0xFF68d3e8),
        onHigh = Color.Black,
        normal = Color(0xFF88ceeb),
        onNormal = Color.Black,
        low = Color(0xFFa7dcd8),
        onLow = Color.Black,
        lowest = Color(0xFFd9f5f0),
        onLowest = Color.Black,
    )

    public val green: BrutalColors = BrutalColors(
        bright = Color(0xFF2FFF2F),
        onBright = Color.Black,
        high = Color(0xFF7fbc8c),
        onHigh = Color.Black,
        normal = Color(0xFF91ed91),
        onNormal = Color.Black,
        low = Color(0xFFbafda2),
        onLow = Color.Black,
        lowest = Color(0xFFC9E7C1),
        onLowest = Color.Black,
    )

    public val red: BrutalColors = BrutalColors(
        bright = Color(0xFFFF4911),
        onBright = Color.White,
        high = Color(0xFFff6b6b),
        onHigh = Color.Black,
        normal = Color(0xFFff7a5c),
        onNormal = Color.Black,
        low = Color(0xFFfea079),
        onLow = Color.Black,
        lowest = Color(0xFFf7d6b4),
        onLowest = Color.Black,
    )

    public val orange: BrutalColors = BrutalColors(
        bright = Color(0xFFFF8C00),
        onBright = Color.Black,
        high = Color(0xFFff9500),
        onHigh = Color.Black,
        normal = Color(0xFFffa726),
        onNormal = Color.Black,
        low = Color(0xFFffb74d),
        onLow = Color.Black,
        lowest = Color(0xFFffe0b2),
        onLowest = Color.Black,
    )

    public val yellow: BrutalColors = BrutalColors(
        bright = Color(0xFFFFFF00),
        onBright = Color.Black,
        high = Color(0xFFe2a017),
        onHigh = Color.Black,
        normal = Color(0xFFf4d839),
        onNormal = Color.Black,
        low = Color(0xFFffdc58),
        onLow = Color.Black,
        lowest = Color(0xFFfcfd96),
        onLowest = Color.Black,
    )

    public val pink: BrutalColors = BrutalColors(
        bright = Color(0xFFFF00F5),
        onBright = Color.White,
        high = Color(0xFFff68b5),
        onHigh = Color.Black,
        normal = Color(0xFFffb3ed),
        onNormal = Color.Black,
        low = Color(0xFFffc0ca),
        onLow = Color.Black,
        lowest = Color(0xFFfbdfff),
        onLowest = Color.Black,
    )

    public val purple: BrutalColors = BrutalColors(
        bright = Color(0xFF9D00FF),
        onBright = Color.White,
        high = Color(0xFFD470FF),
        onHigh = Color.Black,
        normal = Color(0xFFa488ef),
        onNormal = Color.Black,
        low = Color(0xFFc5a1ff),
        onLow = Color.Black,
        lowest = Color(0xFFe3e0f3),
        onLowest = Color.Black,
    )

    /** Deep amber/vermilion — between orange and red */
    public val vermilion: BrutalColors = BrutalColors(
        bright = Color(0xFFFF5722),
        onBright = Color.White,
        high = Color(0xFFe86A45),
        onHigh = Color.Black,
        normal = Color(0xFFFF8A65),
        onNormal = Color.Black,
        low = Color(0xFFFFAB91),
        onLow = Color.Black,
        lowest = Color(0xFFfbe9e7),
        onLowest = Color.Black,
    )

    /** Dark crimson/maroon — hazardous, emergency level */
    public val maroon: BrutalColors = BrutalColors(
        bright = Color(0xFFB71C1C),
        onBright = Color.White,
        high = Color(0xFFC62828),
        onHigh = Color.White,
        normal = Color(0xFFD32F2F),
        onNormal = Color.White,
        low = Color(0xFFE57373),
        onLow = Color.Black,
        lowest = Color(0xFFFFCDD2),
        onLowest = Color.Black,
    )

    public val lime: BrutalColors = BrutalColors(
        bright = Color(0xFFCCFF00),
        onBright = Color.Black,
        high = Color(0xFFD4E157),
        onHigh = Color.Black,
        normal = Color(0xFFDCE775),
        onNormal = Color.Black,
        low = Color(0xFFE6EE9C),
        onLow = Color.Black,
        lowest = Color(0xFFF0F4C3),
        onLowest = Color.Black,
    )

    public val all: List<BrutalColors> = listOf(
        blue,
        green,
        red,
        orange,
        yellow,
        pink,
        purple,
        vermilion,
        maroon,
        lime,
    )
}
