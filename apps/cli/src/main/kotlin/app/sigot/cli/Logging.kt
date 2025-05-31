package app.sigot.cli

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.mordant.rendering.TextColors

fun SuspendingCliktCommand.initLogger(level: String = "info") =
    apply {
        val writer = object : LogWriter() {
            override fun log(
                severity: Severity,
                message: String,
                tag: String,
                throwable: Throwable?,
            ) {
                val log = buildString {
                    if (tag.isNotBlank()) {
                        append("[$tag] ")
                    }

                    val color = when (severity) {
                        Severity.Verbose -> TextColors.gray
                        Severity.Debug -> TextColors.magenta
                        Severity.Info -> TextColors.blue
                        Severity.Warn -> TextColors.yellow
                        Severity.Error -> TextColors.red
                        Severity.Assert -> TextColors.brightRed
                    }
                    append(color("${severity.name}: $message"))
                }

                val canLog = when (level.lowercase()) {
                    "verbose" -> true
                    "debug" -> severity >= Severity.Debug
                    "info" -> severity >= Severity.Info
                    else -> true
                }

                if (canLog) {
                    echo(log, err = severity >= Severity.Warn)
                }
                throwable?.stackTraceToString()?.let { err ->
                    echo(TextColors.red(err), err = true)
                }
            }
        }

        Logger.setLogWriters(writer)
    }
