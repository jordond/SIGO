package now.shouldigooutside.cli.util

import co.touchlab.kermit.Logger
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.mordant.rendering.TextColors
import now.shouldigooutside.cli.initLogger

abstract class BaseCommand(
    name: String? = null,
) : SuspendingCliktCommand(name) {
    private val logLevel by option(
        "--log",
        "--log-level",
        "--level",
        help = "Set the log level",
    ).choice(VERBOSE, DEBUG, INFO).default(INFO)

    override suspend fun run() {
        initLogger(logLevel)
        execute()
    }

    abstract suspend fun execute()

    internal fun success(
        text: String,
        newLine: Boolean = true,
    ) = output(INFO, TextColors.green(text), trailingNewline = newLine)

    internal fun failure(
        text: String,
        newLine: Boolean = true,
    ) = output(INFO, TextColors.red(text), trailingNewline = newLine)

    internal fun error(
        text: String,
        cause: Throwable? = null,
    ) = Logger.e(cause) { text }

    internal fun error(
        cause: Throwable? = null,
        block: () -> String,
    ) = Logger.e(cause) { block() }

    internal fun info(
        text: String,
        newLine: Boolean = true,
    ) = output(INFO, TextColors.blue(text), trailingNewline = newLine)

    internal fun warn(
        text: String,
        newLine: Boolean = true,
    ) = output(INFO, TextColors.yellow(text), trailingNewline = newLine)

    internal fun debug(
        text: String,
        newLine: Boolean = true,
    ) = output(DEBUG, TextColors.magenta(text), trailingNewline = newLine)

    internal fun verbose(
        text: String,
        newLine: Boolean = true,
    ) = output(VERBOSE, TextColors.cyan(text), trailingNewline = newLine)

    private fun output(
        level: String,
        text: Any,
        trailingNewline: Boolean,
    ) {
        val shouldOutput = when (level) {
            VERBOSE -> logLevel == VERBOSE
            DEBUG -> logLevel == DEBUG || logLevel == VERBOSE
            else -> true
        }

        if (shouldOutput) {
            echo(text, trailingNewline = trailingNewline)
        }
    }

    companion object {
        private const val VERBOSE = "verbose"
        private const val DEBUG = "debug"
        private const val INFO = "info"
    }
}
