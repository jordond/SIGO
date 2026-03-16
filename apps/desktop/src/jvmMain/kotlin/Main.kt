import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.FileKit
import now.shouldigooutside.App
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.app_name
import now.shouldigooutside.core.resources.ic_cyclone
import now.shouldigooutside.di.initKoin
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension
import java.awt.Toolkit

// Define thresholds for small screens (adjust as needed)
private val smallScreenThreshold = 1800.dp

// Calculate window size as a fraction of screen size for larger screens
private const val WINDOW_WIDTH_FRACTION = 0.8f
private const val WINDOW_HEIGHT_FRACTION = 0.8f

private val defaultWidth = 800.dp
private val defaultHeight = 600.dp

// Predefined sizes for different modes
private val sizes = mapOf(
    "phone" to Pair(360.dp, 800.dp),
    "desktop" to Pair(defaultWidth, defaultHeight),
    "fullscreen" to Pair(Dp.Unspecified, Dp.Unspecified),
)

private val minimumSize = Dimension(350, 600)

fun main(args: Array<String>) {
    val mode =
        args
            .toList()
            .windowed(2, 1, false)
            .find { it.first() == "--mode" }
            ?.getOrNull(1) ?: "desktop"

    val (width, height, placement) =
        if (mode == "fullscreen") {
            calculateFullScreenSize()
        } else {
            val windowWidth = sizes[mode]?.first ?: defaultWidth
            val windowHeight = sizes[mode]?.second ?: defaultHeight
            Triple(windowWidth, windowHeight, WindowPlacement.Floating)
        }

    Logger.i { "Starting application in mode: $mode" }
    Logger.i { "Window size: $width x $height" }

    FileKit.init("now.shouldigooutside.desktop")
    initKoin()

    application {
        Window(
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.ic_cyclone),
            state = rememberWindowState(
                width = width,
                height = height,
                placement = placement,
                position = WindowPosition(Alignment.Center),
            ),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = minimumSize
            App()
        }
    }
}

private fun calculateFullScreenSize(): Triple<Dp, Dp, WindowPlacement> {
    // Get the current width and height of the screen
    val dimensions = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = dimensions.width.dp
    val screenHeight = dimensions.height.dp

    Logger.d { "Screen size: $screenWidth x $screenHeight" }

    val windowWidth = (screenWidth * WINDOW_WIDTH_FRACTION).coerceAtMost(1920.dp)
    val windowHeight = (screenHeight * WINDOW_HEIGHT_FRACTION).coerceAtMost(1080.dp)

    // Determine window placement based on screen size
    val windowPlacement = if (screenWidth <= smallScreenThreshold) {
        WindowPlacement.Maximized
    } else {
        WindowPlacement.Floating
    }

    return Triple(windowWidth, windowHeight, windowPlacement)
}
